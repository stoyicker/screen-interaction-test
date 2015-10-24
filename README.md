# screen-interaction-test
Test project for Screen Interaction. Jorge Antonio Diaz-Benito Soriano.

[![!](https://magnum.travis-ci.com/stoyicker/screen-interaction-test.svg?token=FQiyriPMi5DyTTCWNBup&branch=master)](https://magnum.travis-ci.com/stoyicker/screen-interaction-test)

# Notes
* The wireframes were a bit confusing as for how to show the loading progressbar, as the screenshot was showing it on the center while the PDF was doing so next to the sorting button. Hesitant, I decided to take the approach of the currently standard interaction: a swipe-to-refresh list.
* The buildscript describes only the debug buildType; a specific release binary is not built as no keys were provided nor usage of ProGuard or resource shrinkage tools was mentioned in the requirements.
* Instead of using StateDrawables, which I think is what the wireframes hint, I chose to use ripple animations as they agree current design guidelines better.
* Supports all screen orientations.
* The easter egg is in the contact detail screen. Can you find it? (less funny if you don't have Twitter installed)
