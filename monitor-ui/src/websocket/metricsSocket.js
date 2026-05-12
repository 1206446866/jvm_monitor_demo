const socket = new WebSocket("ws://localhost:8080/ws/metrics");

socket.onopen = () => {
    console.log("WebSocket connected");
};

socket.onclose = () => {
    console.log("WebSocket closed");
};

socket.onerror = (err) => {
    console.error("WebSocket error:", err);
};

export default socket;