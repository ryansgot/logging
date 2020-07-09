# Logging Android Appcenter

Builds upon the [logging](../logging/README.md) and [logging-android](../logging-android/README.md) libraries, providing specific loggers for Microsoft AppCenter.

## Features

See the [provider definition](../android-appcenter-testapp/src/main/AndroidManifest.xml) for an example usage of this provider. Pay special attention to the `meta-data` tag added to the provider, where you define the secret key for your app in AppCenter. Storage of this key in source control is not recommended, and the example (see the build.gradle.kts) shows how you can store it as a gradle property that gets added as a manifest property.