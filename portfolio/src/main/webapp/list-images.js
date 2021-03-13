/** Fetches the list of images from the server and adds it to the gallery page. */
async function showImageGallery() {
    const responseFromServer = await fetch('/list-images');
    const imageLinks = await responseFromServer.json();
    // Add images to gallery
    const galleryContainer = document.getElementById('gallery-photos');
    for (var i = 0; i < imageLinks.length; i++) {
        var img = new Image();
        img.setAttribute("src", imageLinks[i]);
        galleryContainer.appendChild(img);
    }
}