/** Fetches the list of images from the server that have been organized and adds them to the analyzed page. */
async function showAnalyzedGallery() {
    const responseFromServer = await fetch('/list-analyzed-images');
    const labelsToPictures = await responseFromServer.json();
    // Add images to gallery
    const galleryContainer = document.getElementById('gallery-container');
    for (var i = 0; i < labelsToPictures.length; i++) {
        var header = document.createElement('h3');
        var imageLinks = labelsToPictures[i].imageURLs;
        header.append(labelsToPictures[i].description);
        galleryContainer.appendChild(header);
        var innerGallery = document.createElement('div');
        innerGallery.className = "image-thumbnails"
        imageLinks.forEach(link => {
            var img = new Image();
            img.setAttribute("src", link);
            innerGallery.appendChild(img);
        })
        galleryContainer.appendChild(innerGallery);
    }
}