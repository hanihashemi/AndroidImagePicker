### Android Image Picker
[![](https://jitpack.io/v/hanihashemi/AndroidImagePicker.svg)](https://jitpack.io/#hanihashemi/AndroidImagePicker)

![Android Image Picker](/screenshot.gif)

- Don't worry about various devices/OS variations.
- Don't worry about out-of-memory errors.
- Don't worry about creating thumbnails to show a preview.
- Choose images from device or take a photo
- Get all metadata about the media that you would probably need
- And YES you can crop your images :)
## Usage
``` java
PickerImpl picker;

void pickMultipleImageFromGallery() {
   picker = new ImagePicker.Builder(this, this)
      .allowMultiple(true)
      .ensureMaxSize(500, 500)
      .shouldGenerateMetadata(false)
      .shouldGenerateThumbnails(true)
      .setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR)
      .build();
      picker.pickImage();
}

void pickSingleImageFromGallery() {
   picker = new ImagePicker.Builder(this, this)
      .shouldCrop(true) // <============= For croping
      .build();
   picker.pickImage();
}

void takePictureByCamera() {
   picker = new CameraImagePicker.Builder(this, this)
      .build();
   picker.pickImage();
}

@Override
void onActivityResult(int requestCode, int resultCode, Intent data) {
   super.onActivityResult(requestCode, resultCode, data);
   picker.getActivityResult(this, requestCode, resultCode, data);
}

@Override
void onImagesChosen(List<ChosenImage> images) {
   MediaResultsAdapter adapter = new MediaResultsAdapter(images, this);
   lvResults.setAdapter(adapter);
   for (ChosenImage image : images) {
      Log.d(TAG, "==> original image path: " + image.getOriginalPath());
      Log.d(TAG, "==> big thumbnail image path: " + image.getThumbnailPath());
      Log.d(TAG, "==> small thumbnail image path: " + image.getThumbnailSmallPath());
   }
}

@Override
void onError(String message) {
   Toast.makeText(this, message, Toast.LENGTH_LONG).show();
}
```

## Installation
<b>Step 1:</b> Add the JitPack repository to your build file
``` groovy
allprojects {
   repositories {
      ...
      maven { url 'https://jitpack.io' }
   }
}
```
	
<b>Step 2:</b> Add the dependency
``` groovy
dependencies {
   compile 'com.github.hanihashemi:AndroidImagePicker:1.0'
}
```

##### License
---

Copyright 2016 Kumar Bibek

Licensed under the Apache License, Version 2.0 (the "License");<br />
you may not use this file except in compliance with the License.<br />
You may obtain a copy of the License at
   
[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
	
Unless required by applicable law or agreed to in writing, software<br />
distributed under the License is distributed on an "AS IS" BASIS,<br />
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br />
See the License for the specific language governing permissions and<br />
limitations under the License.

---
