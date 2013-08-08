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

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MusicPlayer {
	
	enum PlayState {
		IDLE,
		PLAY,
		PAUSE,
		STOP
	}
	
	private static final int UNKNOWN = -1;
	
	class MusicPlayerState {
		boolean audioFocusGranted = false;
		boolean userInitiatedState = false;
		boolean wasPlayingWhenTransientLoss = false;
		boolean volumeLowered = false;
		boolean released = false;
		int lastKnownAudioFocusState = UNKNOWN;
		PlayState state = PlayState.IDLE;
		public boolean looping;
	}
	
	private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
	private OnCompletionListener onComplete = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			if(mState.audioFocusGranted) {
				AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
				am.abandonAudioFocus(mOnAudioFocusChangeListener);
				teardown();
			}
		}
	};
    private Context mContext;
    private int mResourceId;
    private MediaPlayer mPlayer;
    private MusicPlayerState mState = new MusicPlayerState();
    
    public MusicPlayer(Context context, int resId) {
    	mContext = context;
    	mResourceId = resId;
    	mPlayer = MediaPlayer.create(context, resId);
    	mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
    		
    		@Override
    		public void onAudioFocusChange(int focusChange) {
    			switch (focusChange) {
    			case AudioManager.AUDIOFOCUS_GAIN:
    				mState.audioFocusGranted = true;
    				
    				if(mState.released) {
						initializeMediaPlayer();
					}
    				
    				switch(mState.lastKnownAudioFocusState) {
    				case UNKNOWN:
    					if(mState.state == PlayState.PLAY && !mPlayer.isPlaying()) {
    						mPlayer.start();
    					}
    					break;
    				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
    					if(mState.wasPlayingWhenTransientLoss) {
    						mPlayer.start();
    					}
    					break;
    				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
    					restoreVolume();
    					break;
    				}
    				
    				break;
    			case AudioManager.AUDIOFOCUS_LOSS:
    				mState.userInitiatedState = false;
    				mState.audioFocusGranted = false;
    				teardown();
    				break;
    			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
    				mState.userInitiatedState = false;
    				mState.audioFocusGranted = false;
    				mState.wasPlayingWhenTransientLoss = mPlayer.isPlaying();
    				mPlayer.pause();
    				break;
    			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
    				mState.userInitiatedState = false;
    				mState.audioFocusGranted = false;
    				lowerVolume();
    				break;
    			}
    			mState.lastKnownAudioFocusState = focusChange;
    		}
    	};
    	
    	AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    	
		int result = am.requestAudioFocus(mOnAudioFocusChangeListener,
				// Use the music stream.
				AudioManager.STREAM_MUSIC,
				// Request permanent focus.
				AudioManager.AUDIOFOCUS_GAIN);
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			mState.audioFocusGranted = true;
		} else if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
			mState.audioFocusGranted = false;
		}
    }
    
    public void loop() {
    	mState.looping = true;
    }
    
    public void play() {
    	mState.userInitiatedState = true;
    	mState.state = PlayState.PLAY;
    	if(mState.released && mPlayer==null) {
    		initializeMediaPlayer();
    	}
    	if(!mPlayer.isPlaying() && mState.audioFocusGranted){
    		mPlayer.start();
    	}
    }
    
    private void initializeMediaPlayer() {
		mPlayer = MediaPlayer.create(mContext, mResourceId);
		if(mState.looping) {
			mPlayer.setLooping(true);
			mPlayer.setOnCompletionListener(onComplete);
		}
	}
    
    private void lowerVolume() {
    	mPlayer.setVolume(0.3F, 0.3F);
    }
    
    private void restoreVolume() {
    	mPlayer.setVolume(1F, 1F);
    }
    
    private void teardown() {
    	mPlayer.stop();
    	mPlayer.release();
    	mState.released = true;
    	mPlayer = null;
    }
    
    public void pause() {
    	mState.userInitiatedState = true;
    	mState.state = PlayState.PAUSE;
    	if(mPlayer!=null) {
    		mPlayer.pause();
    	}
    }
    
    public void stop() {
    	mState.userInitiatedState = true;
    	mState.state = PlayState.STOP;
    	if(mPlayer!=null) {
    		teardown();
    	}
    }
    
}
