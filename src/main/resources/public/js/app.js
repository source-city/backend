var events = new EventSource('http://localhost:8080/api/updates');

events.onmessage = function (msg) {
    console.log(msg);
};
