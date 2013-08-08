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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private MusicPlayer mMusicPlayer;
	private boolean isPlaying = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mMusicPlayer = new MusicPlayer(this, R.raw.mixdown);
		mMusicPlayer.loop();
		setContentView(R.layout.activity_main);
	}
	
	public void playpause(View v) {
		if(isPlaying) {
			((Button)v).setText(R.string.play);
			mMusicPlayer.pause();
		} else {
			((Button)v).setText(R.string.pause);
			mMusicPlayer.play();
		}
		isPlaying = !isPlaying;
	}
	
	public void stop(View v) {
		((Button)findViewById(R.id.button1)).setText(R.string.play);
		mMusicPlayer.stop();
		isPlaying = false;
	}
	
	public void startOtherActivity(View v) {
		Intent otherActivityIntent = new Intent(this, InteruptSoundsActivity.class);
		startActivity(otherActivityIntent);
	}
}
