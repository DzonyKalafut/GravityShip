/*******************************************************************************
 * Copyright 2013 Ján Hajnár.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.hajnar.GravityShip;

public class GameSettings {

    private boolean soundOn;
    private boolean musicOn;
    private boolean vibrationOn;
    private int recentWorld;
    private int numOfWorlds;
//	private float [][] bestTimes;

    public GameSettings() {
        this.soundOn = false;
        this.musicOn = false;
        this.vibrationOn = false;
        this.recentWorld = 999;
        this.numOfWorlds = 999;
//		this.bestTimes = new float[10][5];
    }

    public boolean isSoundOn() {
        return soundOn;
    }

    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
    }

    public boolean isMusicOn() {
        return musicOn;
    }

    public void setMusicOn(boolean musicOn) {
        this.musicOn = musicOn;
    }

    public int getRecentWorld() {
        return recentWorld;
    }

    public void setRecentWorld(int recentLevel) {
        this.recentWorld = recentLevel;
    }

    public int getNumOfWorlds() {
        return numOfWorlds;
    }

    public void setNumOfWorlds(int numOfWorlds) {
        this.numOfWorlds = numOfWorlds;
    }

    public boolean isVibrationOn() {
        return vibrationOn;
    }

    public void setVibrationOn(boolean vibrationOn) {
        this.vibrationOn = vibrationOn;
    }

//	public float[][] getBestTimes() {
//		return bestTimes;
//	}
//
//	public void setBestTimes(float[][] bestTimes) {
//		this.bestTimes = bestTimes;
//	}


}
