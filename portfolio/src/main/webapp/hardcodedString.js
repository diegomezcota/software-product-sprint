/** Fetches the hard-coded string from the server and adds it to the page. */
async function showHardcodedString() {
    const responseFromServer = await fetch('/get-message');
    const textFromResponse = await responseFromServer.text();

    const msgContainer = document.getElementById('server-message-container');
    msgContainer.innerText = textFromResponse;
}