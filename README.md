# WildRank V2 - Android
An Android app for scouting at FIRST Robotics Competitions

[![Build Status](https://travis-ci.org/nwalters512/wildrank-android-v2.png?branch=master)](https://travis-ci.org/nwalters512/wildrank-android-v2)
[![Number of issues](https://img.shields.io/github/issues/nwalters512/wildrank-android-v2.svg)]

Created by Team 111 WildStang.

##***Important note***
Team 111 WildStang has only used this app with Motorola Xooms flashed with an AOSP 4.2.2 ROM. This will likely function on other devices, as long as those devices support USB OTG. The only thing likely to change between devices is the mount path of the flash drive. This is not a standardized Android feature. To configure this for your specific setup, you should only have to change the return the return value of ```Utilities.getExternalRootDirectory()``` to point to the correct location. A future enhancement we'd like to make is making this path selectable within the application so that you can deploy the same build to a variety of Android tablets.

Also note that this app is designed to be used on large tablets. It may work on smaller devices, such as a Nexus 7, but we lacked the hardware to test it on any other devices. It will almost certainly not work properly on phones. We decided that the form factor of a tablet is more suitable for scouting than phones, so the layouts are not optimized for phone-sized displays.

## Overview
WildRank is designed to serve as a platform upon which scouting systems can be built on. WildRank provides match scouting, pit scouting, notetaking, data analysis, a whiteboard, and more. The framework offers checkboxes, counters, text boxes, number fields, and dropdown spinners to be used for data collection.

WildRank includes a user system that allows the application to track who inputs data. It also includes a rudimentary permissions system that allows users marked as "admins" to access app settings and additional app features.

WildRank is designed to function without an internet connection or wireless connectivity. Data is stored locally and synced between devices using a flash drive.

This app relies on a companion desktop app for initial configuration, which can be found at [this repository](https://github.com/nwalters512/wildrank-desktop-v2). That app downloads the appropriate list of matches/teams for a given event and puts them on a flash drive, which is then synced to all the tablets to configure them for the given event. It also allows the list of users to be managed.

### Data Storage & Syncing
Unlike the first iteration of WildRank where data was represented as JSON strings in text files, WildRank v2 uses [Couchbase Lite](http://www.couchbase.com/nosql-databases/couchbase-mobile) databases to store data both internally and on the flash drive. This more structured approach allows the use of a single database for all information and generally makes it easier to access data.

WildRank is designed to function without any internet connection after the initial setup. Syncing is done via a flash drive using a USB OTG (On-The-Go) adapter. The adapter is required to connect he flash drive to Android devices. Note that this means that your android device(s) must suppport the OTG protocol; if they do not, you may be able to root your device in order to add support.

Unfortunately, Couchbase Mobile doesn't support local-to-local syncing on Android, so a custom syncing algorithm was implemented in WildRank. It's rather complex and inefficient, but it gets the job done. For the implementation of syncing, see ```SyncActivity```.

### Configuring WildRank for your team
WildRank is designed to be easy to update for each new game. Most of the app can stay the same year-to-year: team lists, storing data, syncing data, notetaking, etc. Most changes that will need to be made are done via the XML layout files.

#### Configuring match scouting
The relevant XML files for match scouting are:
 * ```fragment_scout_autonomous.xml```
 * ```fragment_scout_teleop.xml```
 * ```fragment_scout_post_match.xml```

To add a field to a scouting page, simply include a ```View``` that extends ```ScoutingView```s in the appropriate layout file. WildRank provides several subclasses of ```ScoutingView```, including:
 * ```ScoutingCheckboxView```
 * ```ScoutingCounterView```
 * ```ScoutingNumberView```
 * ```ScoutingSpinnerView```
 * ```SerializableTextView```

If the functionality you want isn't provided by any of the included widgets, you can make your own view that extends ```ScoutingView```. See any of the included views for an example of how such a class should behave. You can look at ```ScoutingStacksView``` for a more complex and highly specialized implementation.

Each defined view in the XML layout files should provide a ```key``` attribute. The key is used to tell the framework how to store the value when the match results are saved. When the Couchbase document representing the results is constructed, ```key``` will be used as the key in the document. It is up to subclasses of ```ScoutingView``` to generate and write a value that corresponds to this key.

An example layout file is included below. It contains two columns. The first is titled "General" and has a single checkbox for recording if a robot moves. The second is titled "Scoring" and has a counter that counts how many balls were scored in the hot high goal.

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:custom="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/light_gray" >

    <!-- General -->

    <LinearLayout
        android:id="@+id/general"
        android:layout_width="250dp"
        android:layout_height="wrap_content"
        android:layout_alignParentLeft="true"
        android:layout_alignParentTop="true"
        android:layout_marginRight="10dp"
        android:orientation="vertical" >
        
        <TextView
            android:id="@+id/general_title"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="General"
            android:textSize="30sp" />

        <org.wildstang.wildrank.androidv2.views.ScoutingCheckboxView
            android:id="@+id/autonomous_move"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            custom:key="autonomous-moved"
            custom:label="Moved" />
    </LinearLayout>

    <!-- Scoring -->

    <LinearLayout
        android:id="@+id/scoring"
        android:layout_width="250dp"
        android:layout_height="wrap_content"
        android:layout_alignParentTop="true"
        android:layout_toRightOf="@id/general"
        android:orientation="vertical" >

        <TextView
            android:id="@+id/scoring_title"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Scoring"
            android:textSize="30sp" />

        <org.wildstang.wildrank.androidv2.views.ScoutingCounterView
            android:id="@+id/autonomous_hot_goal_high_scored"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            custom:key="autonomous-scored_hot_high"
            custom:label="Hot Goal High" />
    </LinearLayout>

</RelativeLayout>
```

A possible output, given this layout, for match 32 of team 111 would look like this:

```
{
    "team_numner": 111,
    "match_number": 32,
    ...,
    "data" {
        "autonomous-moved": true,
        "autonomous-scored_hot_high": 3
    }
}
```

#### Configuring pit scouting
Pt scouting is handled very similarly to match scouting. Simply incude any number of ```ScoutingView```s in the layout and provide a ```key``` for each one. All changes for pit scouting should be made in ```fragment_scout_pit.xml```

#### Configuring the team summary
The Team Summary mode provides the ability to show a summary of all the data that was collected for a given team. It consists of several tabs/sections, each of which will be described below.

##### Configuring the Info tab
The info tab displays a general overview of a team, including number, name, picture, notes, and pit data. Number, name, picture, and notes are all handled automatically by WildRank. However, displaying pit data depends on some specific configuration.

Pit data is displayed with ```TemplatedTextView```s. A ```TamplatedTextView``` will dynamically substitute data into the string that is displayed. It will use any text eclosed by double braces ```{{...}}``` as a key to get a value from a JSON object. It works similary to the ```key``` from pit/match scouting. The best way to explain this is to provide an example.

Say I have the following pit data JSON object and I want to display the ```robot_weight``` field from it:

```json
{
    "team_number": 111,
    "scouted_by": "SCOUTER NAME",
    ...,
    "data": {
        "robot_weight": 120
    }
}
```

I would create a ```TemplatedTextView``` in my layout and add a ```text``` attribute to the view (note that this should be in the project xml namespace, not the android one; for instance, you might use ```custom:text``` instead of ```android:text```). The ```text``` attribute should consist of a standard string with double braces surrounding the key of the data I wish to substitute. So, to display the robot weight, I would add the attribute ```custom:text="&lt;b&gt;Weight:&lt;/b&gt; {{robot_weight}} lbs"```. This would display as

**Weight**: 120 lbs
 
Note that the ```text``` attribute is parsed as HTML, so you can use basic HTML formatting to add bold, italics, etc. to your text strings.

The setup for the pit summary is done in the layout file ```fragment_summaries_info.xml```

#### Configuring the match data view
Team Summary mode also provides basic data analysis in the Match Data tab. It uses subclasses of ```MatchDataView``` to display data. The implementation of these are specific to the data collected for a specific year, so no default implementations are offered by WildRank. However, it's very easy to build your own ```MatchDataView```. In general, subclasses should only have to override ```calculateFromDocuments(...)```. This method is given a list of all match results documents for the given team that are stored in the database. You can then extract the necessary information and use that to generate displayable text. For instance, here is an example that uses [RxJava](https://github.com/ReactiveX/RxJava) to compute the total number of fouls received by a team:

```java
@Override
public void calculateFromDocuments(List<Document> documents) {
    if (documents == null) {
        return;
    } else if (documents.size() == 0) {
        return;
    }

    Observable foulsObservable = Observable.from(documents)
            .map(doc -> (Map<String, Object>) doc.getProperty("data"))
            .map(data -> data.get("post_match-foul"))
            .filter(fouls -> fouls != null)
            .map(fouls -> (int) fouls);
            
    MathObservable.sumInteger(foulsObservable)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(sum -> setValueText("" + sum), error -> Log.d("wildrank", this.getClass().getName()));
}
```

When you include ```MatchDataView```s in the appropriate layout, they will automagically be given the list of appropriate documents, which you can use in your computations.

The setup for the match view is in the layout file ```fragment_summaries_data.xml```
 
#Contributing
Want to add features, fix bugs, or just poke around the code? No problem!

1. Set up your development environment if you haven't used Android Studio before ([see below](#setup))
2. Fork this repository, import the project to your IDE, and create a branch for your changes
3. Make, commit, and push your changes to your branch
4. Submit a pull request here and we'll review it and and accept it if everything looks good!

For more detailed instructions, checkout [GitHub's Guide to Contributing](https://guides.github.com/activities/contributing-to-open-source/)

We ask that all contributions be team/game agnostic; they should support the framework in general and not be designed for any one game or team. If you don't know if that describes the feature you want to submit, or if you can make the case that your specific feature would be valuable, open an issue and we'll get back to you!

#Environment Setup<a name="setup"></a>

1. Ensure that you have git installed and that it is added to your system's PATH variable. You should be open you system's shell, navigate to a git repository (like this one), run ```git status``` and get data back.
2. If you haven't already, make sure you have the Android development environment set up. You will need to have [Android Studio](https://developer.android.com/sdk/installing/studio.html) installed (this also required the [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)).
3. Make sure you read through some [Tips and Tricks](https://developer.android.com/sdk/installing/studio-tips.html) for developing with Android Studio.
4. Use the [Android SDK Manager](https://developer.android.com/tools/help/sdk-manager.html) to download the correct versions of the Android libraries. You will need to download the Android SDK Tools, Android SDK Platform-Tools, and the SDK Platform for Android 5.1 (API level 22). If you have already downloaded these, double check and make sure they've been updated to the latest version.
5. On the device you want to run the application on, make sure that you have [enabled USB Debugging](http://stackoverflow.com/questions/16707137/how-to-find-and-turn-on-usb-debugging-mode-on-nexus-4) in your Settings menu.
