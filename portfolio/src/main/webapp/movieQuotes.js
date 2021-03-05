/** Fetches the hard-coded string from the server and adds it to the page. */
async function showMovieQuotes() {
    const responseFromServer = await fetch('/get-message');
    const movieQuotes = await responseFromServer.json();
     // Pick a random quote.
    const quote = movieQuotes[Math.floor(Math.random() * movieQuotes.length)];
    // Add quote to page
    const msgContainer = document.getElementById('server-message-container');
    msgContainer.innerText = quote;
}