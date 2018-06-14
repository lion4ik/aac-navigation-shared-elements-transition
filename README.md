 [ ![Download](https://api.bintray.com/packages/lion4ik/maven/arch-navigation-shared-elements/images/download.svg?version=0.1) ](https://bintray.com/lion4ik/maven/arch-navigation-shared-elements/0.1/link)
 
# aac-navigation-shared-elements-transition

aac-navigation-shared-elements-transition allows you to use shared elements transitions between fragments.

![](https://media.giphy.com/media/nbNVIg0jsQMjapzD2w/giphy.gif)
![](https://media.giphy.com/media/3ixX8wy3nM1OJpVBvZ/giphy.gif)

## Usage

#### Add in project

Add specific maven repository to repositories closure. For example, you should add it to root
of `build.gradle`:

```groovy
allprojects {
  repositories {
    maven { url "https://dl.bintray.com/lion4ik/maven" }
  }
}
```
	 
Add dependency:

```groovy
dependencies {
   implementation "com.github.lion4ik:arch-navigation-shared-elements:$version"
}
```

where recommended `$version` is the latest from Download badge  [ ![Download](https://api.bintray.com/packages/lion4ik/maven/arch-navigation-shared-elements/images/download.svg?version=0.1) ](https://bintray.com/lion4ik/maven/arch-navigation-shared-elements/0.1/link).

#### How to use

To integrate with aac-navigation-shared-elements-transition you need to do several steps

1. You need to declare navigation fragment in your xml layout:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <android.support.v7.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/colorPrimary"
        android:theme="@style/ThemeOverlay.AppCompat.Dark"/>
    <fragment
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/my_nav_host_fragment"
        android:name="com.github.lion4ik.arch.sharedelements.NavHostSharedElementsTransitionFragment"
        app:navGraph="@navigation/mobile_navigation"
        app:defaultNavHost="false"
    />
</LinearLayout>
```

2. Fragment which is responsible for navigation to details fragment with shared element transition should implement interface: 
```kotlin
interface HasSharedElements {

    fun getSharedElements(): Map<String, View>

    fun hasReorderingAllowed(): Boolean
}
```

You can do it like this:

```kotlin
class SharedElementFragment : Fragment(), HasSharedElements {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_shared_element, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.imgView).setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.next_action)
        )
    }

    override fun getSharedElements(): Map<String, View> {
        val view = view?.findViewById<View>(R.id.imgView) ?: throw IllegalArgumentException("view is null")
        return mapOf(view.transitionName to view)
    }

    override fun hasReorderingAllowed(): Boolean = true
}
```

`FragmentManager` gets views as shared elements through `getSharedElements()` method implemented by `Fragment`. Shared elements will be add to `FragmentTransaction` by `addSharedElement` method. Also, in some cases you need to set reordering flag for `FragmentTransaction`. 
