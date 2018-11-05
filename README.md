# MetaBalls-LIB-Android

Various UI components that simulate a liquid(metaball) effect.

[<img src="/Artwork/TwoClips.gif" width="400"/>](https://youtu.be/YiJP7YC1rJo)

[See demo on youtube](https://youtu.be/YiJP7YC1rJo)

## Did you mean meatball?
Nope -> metaball! This is a popular effect often used to simulate fluids. Try searching for metaball on Dribbble and you'll find countless concepts mostly made in After Effects.
This library contains 3 different Widgets: MetaballMenu, MetaBallProgressBar and MetaBallPageIndicator. 


## Download
Via Gradle

```
compile 'no.danielzeller:metaballslib:1.0.1'
```
or Maven
```
<dependency>
  <groupId>no.danielzeller</groupId>
  <artifactId>metaballslib</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```

## Metaball Circular Menu

[<img src="/Artwork/Menu2.gif" width="400"/>](https://youtu.be/YiJP7YC1rJo)

From XML:

```xml
<no.danielzeller.metaballslib.menu.CircularMenu
        android:id="@+id/metaBallMenu"
        android:layout_width="match_parent"
        android:layout_height="match_parent"

        app:radius="90dp"
        app:angle_between_menu_items = "45"
        app:delay_between_items_animation="40"
        app:open_animation_duration = "1300"
        app:close_animation_duration = "1000" 
        app:open_interpolator_resource="@anim/overshoot_interpolator"
        app:close_interpolator_resource="@anim/overshoot_interpolator"
        app:position_gravity = "center"
        app:main_button_icon = "@drawable/ic_share_black_24dp"
        app:main_button_color = "@color/pastel_pink"
        app:main_button_icon_color = "@color/white"
        />
```

Then you need to setup an Adapter for the menu

```kotlin
       metaBallMenu.adapter = myAdapter
```
Adapter example:
```kotlin
class MetaBallMenuAdapter(val menuItems: List<MenuItem>) : MetaBallAdapter {

    override fun menuItemIconTint(index: Int): Int {
        return menuItems[index].drawableTint
    }

    override fun menuItemBackgroundColor(index: Int): Int {
        return menuItems[index].backgroundColor
    }

    override fun menuItemIcon(index: Int): Drawable? {
        return menuItems[index].drawable
    }

    override fun itemsCount(): Int {
        return menuItems.count()
    }
}

class MenuItem(val backgroundColor: Int, val drawable: Drawable, val drawableTint: Int)
```

## Metaball Directional Menu
[<img src="/Artwork/Menu4.gif" width="400"/>](https://youtu.be/YiJP7YC1rJo)

```xml
 <no.danielzeller.metaballslib.menu.DirectionalMenu
        android:id="@+id/metaBallMenu"
        android:layout_width="match_parent"
        android:layout_height="match_parent" 

        app:expand_direction="horizontal"
        app:main_button_color="@color/metal"
        app:main_button_icon="@drawable/ic_share_black_24dp"
        app:main_button_icon_color="@color/white"
        app:open_animation_duration="1300"
        app:close_animation_duration="1300"
        app:delay_between_items_animation="80"
        app:open_interpolator_resource="@anim/overshoot_interpolator"
        app:close_interpolator_resource="@anim/overshoot_interpolator"
        app:position_gravity="bottom_left" />
```


## Metaball Page Indicator
[<img src="/Artwork/Pager.gif" width="400"/>](https://youtu.be/YiJP7YC1rJo)

```xml
   <no.danielzeller.metaballslib.MetaBallPageIndicator
        android:id="@+id/metaBallPageIndicator"
        android:layout_width="match_parent"
        android:layout_height="80dp"
       
        app:dot_colors_override_array_id="@array/viewpager_page_indicator_colors"
        app:selected_dot_color="@color/white"
        app:dot_size="30dp"
        app:dot_margin="10dp"
        app:selected_indicator_is_drop_drawable="true"/>
```

Then you need to attach the ViewPager to the MetaBallPageIndicator

```kotlin
 metaBallPageIndicator.attachToViewPager(bottomViewPager)
 ```

## Metaball Progress Bar 
[<img src="/Artwork/Menu3.gif" width="400"/>](https://youtu.be/YiJP7YC1rJo)
```xml
      <no.danielzeller.metaballslib.progressbar.MBProgressBar
          android:id="@+id/mbProgressBar"
          android:layout_width="80dp"
          android:layout_height="80dp"
  
          app:progressbar_type="circular"
          app:colors_array_id="@array/gradient_green"
          app:rotate="true" />
```

#### ProgressBar Type 
Set app:progressbar_type to one of the following: circular, blobs, dots, eight, square or long_path for a different look.

[<img src="/Artwork/Menu5.gif" width="400"/>](https://youtu.be/YiJP7YC1rJo) 

## Contact

You can reach me on Twitter as [@zellah](https://twitter.com/zellah) or [email](mailto:hello@danielzeller.no).


## Who's behind this?

Developed by Daniel Zeller - [danielzeller.no](http://danielzeller.no/), a freelance developer situated in Oslo, Norway.

