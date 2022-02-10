/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package ml.lacmus.app.ml;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.List;

/** Generic interface for interacting with different recognition engines. */
public interface Detector {
  Recognition recognizeImage(Bitmap bitmap);

  void enableStatLogging(final boolean debug);

  String getStatString();

  void close();

  void setNumThreads(int numThreads);

  void setUseNNAPI(boolean isChecked);

  /** An immutable result returned by a Detector describing what was recognized. */
  class Recognition {
    /**
     * A unique identifier for what has been recognized. Specific to the class, not the instance of
     * the object.
     */
    private final String id;

    /** Display name for the recognition. */
    private final String title;

    /** Display name for the recognition. */
    private final Float confidence;

    /** Optional location within the source image for the location of the recognized object. */
    private List<Rect> locations;

    public Recognition(
            final String id, final String title, Float confidence, final List<Rect> locations) {
      this.id = id;
      this.title = title;
      this.confidence = confidence;
      this.locations = locations;
    }

    public String getId() {
      return id;
    }

    public Float getConfidence() {
      return confidence;
    }

    public List<Rect> getLocations() {
      return locations;
    }

    public void setLocations(List<Rect> locations) {
      this.locations = locations;
    }


  }

}
