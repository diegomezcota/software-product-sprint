/** Fetches the list of greetings from datastore and adds it to the greetings page. */
async function showGreetings() {
    const responseFromServer = await fetch('/list-greetings');
    const greetings = await responseFromServer.json();
    // Add greetings to page
    const greetingsContainer = document.getElementById('greetings-list');
    for (var i = 0; i < greetings.length; i++) {
        var greetingElement = document.createElement("li");
        greetingElement.appendChild(document.createTextNode(greetings[i].greeting));
        greetingsContainer.appendChild(greetingElement);
    }
}