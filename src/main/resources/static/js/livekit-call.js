let livekitRoom = null;
let micEnabled = true;
let cameraEnabled = true;
let screenEnabled = false;

document.addEventListener("DOMContentLoaded", function () {
    setupLiveKitButtons();
});

function setupLiveKitButtons() {
    const joinButton = document.getElementById("joinCallButton");
    const leaveButton = document.getElementById("leaveCallButton");
    const micButton = document.getElementById("toggleMicButton");
    const cameraButton = document.getElementById("toggleCameraButton");
    const screenButton = document.getElementById("toggleScreenButton");

    if (!joinButton) {
        return;
    }

    joinButton.addEventListener("click", joinCall);
    leaveButton.addEventListener("click", leaveCall);
    micButton.addEventListener("click", toggleMic);
    cameraButton.addEventListener("click", toggleCamera);
    screenButton.addEventListener("click", toggleScreen);
}

function getCsrfHeadersForLiveKit() {
    const tokenMeta = document.querySelector("meta[name='_csrf']");
    const headerMeta = document.querySelector("meta[name='_csrf_header']");

    if (tokenMeta && headerMeta) {
        return {
            [headerMeta.content]: tokenMeta.content
        };
    }

    const csrfInput = document.querySelector("input[name='_csrf']");

    if (csrfInput) {
        return {
            "X-CSRF-TOKEN": csrfInput.value
        };
    }

    return {};
}

async function joinCall() {
    const callStage = document.querySelector(".call-stage");
    const channelId = callStage ? callStage.dataset.channelId : null;

    if (!channelId) {
        setCallStatus("no channel selected");
        return;
    }

    try {
        setCallStatus("connecting...");

        const response = await fetch(`/web/channels/${channelId}/live/join`, {
            method: "POST",
            headers: getCsrfHeadersForLiveKit()
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error("LiveKit join error:", response.status, errorText);
            setCallStatus("call error: " + response.status);
            return;
        }

        const data = await response.json();

        const LiveKit = findLiveKitClient();

        if (!LiveKit) {
            setCallStatus("LiveKit script not loaded");
            console.error("LiveKit client object was not found on window");
            return;
        }

        const serverUrl = data.serverUrl || data.url || data.liveKitUrl;
        const token = data.participantToken || data.token || data.accessToken;

        if (!serverUrl || !token) {
            setCallStatus("bad livekit response");
            console.error("Expected serverUrl/token, got:", data);
            return;
        }

        livekitRoom = new LiveKit.Room();

        livekitRoom.on(LiveKit.RoomEvent.TrackSubscribed, function (track, publication, participant) {
            addTrackToPage(track, participant, false);
        });

        livekitRoom.on(LiveKit.RoomEvent.TrackUnsubscribed, function (track, publication, participant) {
            track.detach().forEach(function (element) {
                element.remove();
            });

            if (participant) {
                removeSpecificTrack(participant.identity, track.source);
            }
        });

        livekitRoom.on(LiveKit.RoomEvent.ParticipantDisconnected, function (participant) {
            removeParticipantTracks(participant.identity);
        });

        livekitRoom.on(LiveKit.RoomEvent.Disconnected, function () {
            setCallStatus("disconnected");
            disableCallButtons();
            clearVideoArea();
        });

        await livekitRoom.connect(serverUrl, token);

        await livekitRoom.localParticipant.setMicrophoneEnabled(true);
        await livekitRoom.localParticipant.setCameraEnabled(true);

        attachLocalTracks(LiveKit);

        micEnabled = true;
        cameraEnabled = true;
        screenEnabled = false;

        setCallStatus("connected");
        enableCallButtons();

    } catch (error) {
        console.error(error);
        setCallStatus("call error");
    }
}

function findLiveKitClient() {
    if (window.LivekitClient) return window.LivekitClient;
    if (window.LiveKitClient) return window.LiveKitClient;
    if (window.livekitClient) return window.livekitClient;
    return null;
}

function attachLocalTracks(LiveKit) {
    if (!livekitRoom) {
        return;
    }

    livekitRoom.localParticipant.trackPublications.forEach(function (publication) {
        if (publication.track) {
            addTrackToPage(publication.track, livekitRoom.localParticipant, true);
        }
    });
}

async function leaveCall() {
    if (livekitRoom) {
        await livekitRoom.disconnect();
        livekitRoom = null;
    }

    setCallStatus("not connected");
    disableCallButtons();
    clearVideoArea();
}

async function toggleMic() {
    if (!livekitRoom) return;

    micEnabled = !micEnabled;
    await livekitRoom.localParticipant.setMicrophoneEnabled(micEnabled);

    const button = document.getElementById("toggleMicButton");

    button.textContent = micEnabled ? "Mic off" : "Mic on";
}

async function toggleCamera() {
    if (!livekitRoom) return;

    cameraEnabled = !cameraEnabled;
    await livekitRoom.localParticipant.setCameraEnabled(cameraEnabled);

    const button = document.getElementById("toggleCameraButton");
    button.textContent = cameraEnabled ? "Camera off" : "Camera on";

    refreshLocalVideoTiles();
}

async function toggleScreen() {
    if (!livekitRoom) return;

    try {
        screenEnabled = !screenEnabled;
        await livekitRoom.localParticipant.setScreenShareEnabled(screenEnabled);

        const button = document.getElementById("toggleScreenButton");
        button.textContent = screenEnabled ? "Stop sharing" : "Share screen";

        refreshLocalVideoTiles();

    } catch (error) {
        console.error(error);
        setCallStatus("screen share error");
        screenEnabled = false;
        document.getElementById("toggleScreenButton").textContent = "Share screen";
    }
}

function refreshLocalVideoTiles() {
    if (!livekitRoom) return;

    removeParticipantTracks(livekitRoom.localParticipant.identity);

    livekitRoom.localParticipant.trackPublications.forEach(function (publication) {
        if (publication.track) {
            addTrackToPage(publication.track, livekitRoom.localParticipant, true);
        }
    });
}

function addTrackToPage(track, participant, isLocal) {
    const videoArea = document.getElementById("videoArea");
    if (!videoArea || !track) return;

    if (track.kind === "audio") {
        if (isLocal) return;

        const element = track.attach();
        videoArea.appendChild(element);
        return;
    }

    removePlaceholder();

    const trackKey = participant.identity + "-" + track.source;
    if (document.querySelector(`[data-participant="${trackKey}"]`)) {
        return;
    }

    const element = track.attach();
    if (!element) return;

    const wrapper = document.createElement("div");
    wrapper.className = "video-tile-wrapper";
    wrapper.dataset.participant = trackKey;

    if (track.source === "screen_share") {
        wrapper.classList.add("screen-wrapper");
    }

    element.classList.add("video-tile");
    if (track.source === "screen_share") {
        element.classList.add("screen-tile");
    }

    const label = document.createElement("div");
    label.className = "video-label";
    label.textContent = isLocal ? "You" : participant.identity;
    if (track.source === "screen_share") {
        label.textContent += " (Screen)";
    }

    wrapper.appendChild(element);
    wrapper.appendChild(label);
    videoArea.appendChild(wrapper);
    updateVideoLayout();
}

function removeParticipantTracks(participantIdentity) {
    document.querySelectorAll(`[data-participant^="${participantIdentity}-"]`).forEach(function (element) {
        element.remove();
    });

    restorePlaceholderIfEmpty();
    updateVideoLayout();
}

function removeSpecificTrack(participantIdentity, trackSource) {
    const trackKey = participantIdentity + "-" + trackSource;
    document.querySelectorAll(`[data-participant="${trackKey}"]`).forEach(function (element) {
        element.remove();
    });

    restorePlaceholderIfEmpty();
    updateVideoLayout();
}

function removePlaceholder() {
    const placeholder = document.querySelector(".video-placeholder");
    if (placeholder) {
        placeholder.remove();
    }
}

function restorePlaceholderIfEmpty() {
    const videoArea = document.getElementById("videoArea");
    if (!videoArea) return;

    const activeTiles = videoArea.querySelectorAll(".video-tile-wrapper");
    if (activeTiles.length === 0) {
        clearVideoArea();
    }
}

function clearVideoArea() {
    const videoArea = document.getElementById("videoArea");
    if (!videoArea) return;

    videoArea.innerHTML = `
        <div class="video-placeholder">
            Join the call to see video here
        </div>
    `;
    updateVideoLayout();
}

function enableCallButtons() {
    document.getElementById("joinCallButton").disabled = true;
    document.getElementById("leaveCallButton").disabled = false;
    document.getElementById("toggleMicButton").disabled = false;
    document.getElementById("toggleCameraButton").disabled = false;
    document.getElementById("toggleScreenButton").disabled = false;
}

function disableCallButtons() {
    document.getElementById("joinCallButton").disabled = false;
    document.getElementById("leaveCallButton").disabled = true;
    document.getElementById("toggleMicButton").disabled = true;
    document.getElementById("toggleCameraButton").disabled = true;
    document.getElementById("toggleScreenButton").disabled = true;

    document.getElementById("toggleMicButton").textContent = "Mic off";
    document.getElementById("toggleCameraButton").textContent = "Camera off";
    document.getElementById("toggleScreenButton").textContent = "Share screen";
}

function updateVideoLayout() {
    const videoArea = document.getElementById("videoArea");
    if (!videoArea) return;

    videoArea.classList.remove("one-user", "two-users", "three-users", "four-users", "many-users");

    const tiles = videoArea.querySelectorAll(".video-tile-wrapper");
    const count = tiles.length;

    if (count <= 1) {
        videoArea.classList.add("one-user");
    } else if (count === 2) {
        videoArea.classList.add("two-users");
    } else if (count === 3) {
        videoArea.classList.add("three-users");
    } else if (count === 4) {
        videoArea.classList.add("four-users");
    } else {
        videoArea.classList.add("many-users");
    }
}

function setCallStatus(text) {
    const status = document.getElementById("callStatus");
    if (status) {
        status.textContent = text;
    }
}