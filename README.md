# LoadApp

This is an app for the third project of the Android Kotlin Developer Nanodegree.

The starter code of this app can be found here: https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter

The purpose of this project is to test the concepts learnt throughout this part of the course, such as:
- Notifications
- Custom Views
- Drawing on Canvas Objects
- Property Animations (here applied to the custom view in the main activity)
- MotionLayout (here implemented in detail activity, related to the download status)

Previous project: https://github.com/AurSoft/AsteroidRadarApp

Note:
1. download fails if you try to download Retrofit. This is on purpose, to show what happens in case the download fails.
2. in detail activity, you'll see that MotionLayout is nested in ConstraintLayout. This is a workaround to an issue I found, explained here in detail: https://knowledge.udacity.com/questions/595645
   In summary: Nesting MotionLayout in a ConstraintLayout to work around some glitches of TextViews not being correctly displayed when their visibility/text gets changed on onCreate and there's an animation starting automatically.