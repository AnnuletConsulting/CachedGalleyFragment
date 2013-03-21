CachedGalleyFragment
====================

This is a tab fragment that you can use in your Android App when you need to select multiple images in the middle 
of your app flow and then use the selected pics in some manner.  I could not find an image gallery that
created thumbnails, cached them, and let the user select several and then send the results to the next page.  I 
found several that did one of two of those, but none that did all of them.  I also had a need to be able to display 
images from the web (from flickr, picasa, etc.) for selection.

I forked https://github.com/koush/UrlImageViewHelper to https://github.com/waltmoorhouse/UrlImageViewHelper and
that is the source of the jar in the lib folder.

This is a basic app that shows the pics on the phone as well as some images from flickr.  You would launch the app 
by calling an Activity that includes a fragment for the selector. (See activity_main.xml)
