# Logging Android Urban Airship

Builds upon the [logging](../logging/README.md), providing specific loggers for Airship Analytics.

## Configuring

`logging-android-urbanairship` allows for configuration via the following meta-data

### Configuring Urban Airship

You should configure Urban Airship in accordance with [their instructions](https://docs.airship.com/platform/android/getting-started/). The only required library is `com.urbanairship.android:urbanairship-core`. This library places a minium version of `14.0.0`, and it may break if you use a different major version.

### Other configurable options (none are required)

The attribute name if you want to track the screen:
```xml
<meta-data android:name="fsryan.log.screen_attr_name" android:value="fs_screen_name_attr" />
```

The attribute prefix if you want to update a UA identifier:
```xml
<meta-data android:name="fsryan.log.ua.identifier_attrs" android:resource="@array/identifier_attr_string_array" />
```

