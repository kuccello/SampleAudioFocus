/*
  Copyright 2013 Google Inc. All Rights Reserved.
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
      http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.example.sampleaudiofocus;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;

public class InteruptSoundsActivity extends Activity {

	private AudioManager am;
	private OnAudioFocusChangeListener fcl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interupt_sounds);
		am = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		fcl = new OnAudioFocusChangeListener() {
			@Override
			public void onAudioFocusChange(int ignored) {
				// do nothing
			}
		};

	}

	public void playInterupting(View v) {

		int result = am.requestAudioFocus(fcl,
				// Use the music stream.
				AudioManager.STREAM_MUSIC,
				// Request permanent focus.
				AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			MediaPlayer m = MediaPlayer.create(this, R.raw.whoosh);
			m.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					am.abandonAudioFocus(fcl);
				}
			});
			m.start();
		}
	}

	public void playDucking(View v) {
		int result = am.requestAudioFocus(fcl,
				// Use the music stream.
				AudioManager.STREAM_MUSIC,
				// Request permanent focus.
				AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			MediaPlayer m = MediaPlayer.create(this, R.raw.robot);
			m.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					am.abandonAudioFocus(fcl);
				}
			});
			m.start();
		}
	}

}
