package application.core.playback;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.Random;

public class MediaPlayerFx {

    //下侧面板的已播放时间和总时间
    private Label playedTimeLabel;
    private Label totalTimeLabel;

    //控制播放时间的滑动条和进度条
    private Slider songSlider;

    //播放暂停的图片
    private ImageView playImageView;
    //播放器对象
    private Media media;
    private MediaPlayer mediaPlayer;



    //下侧面板的显示歌曲名称和歌手
    private Label musicNameLabel;
    private Label singerLabel;

    //音量滚动条和进度条
    private Slider volumeSlider;



    //释放播放器资源的函数
    private void mediaDestroy() {
        if (mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            this.playImageView.setImage(new Image("image/PlaybackPause.png"));
        }
        playedTimeLabel.setText("00:00");
        songSlider.setValue(0);
        mediaPlayer.stop();
        mediaPlayer.dispose();
        media = null;
        mediaPlayer = null;
        System.gc();
    }

/*    //音乐播放的函数，并且在mediaPlayer播放结束后判断当前的播放模式，执行下一首的播放
    private void mediaAutoPlay(SongInfo song) {
        //记录之前的mediaPlyer之前的音量和是否为静音状态的局部变量
        double volume = 0;
        boolean isMute = false;
        if (mediaPlayer != null) {  //如果mediaPlayer存在对象实例，先释放资源
            isMute = mediaPlayer.isMute();     //记录mediaPlayer是否是静音状态
            volume = mediaPlayer.getVolume();  //记录播放上一首音乐的音量
            this.mediaDestroy();
        }
        this.playImageView.setImage(new Image("image/PlaybackPlay.png"));
        this.musicNameLabel.setText(song.getMusicName());
        this.singerLabel.setText(song.getSinger());
        this.songSlider.setMax(song.getTotalSeconds());
        this.totalTimeLabel.setText(song.getTotalTime());
        media = new Media(new File(song.getSrc()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        if (volume != 0 && isMute) {  //如果之前的mediaPlayer是静音状态，那么设置当前的mediaPlayer也是静音状态，音量为上一个mediaPlayer的音量
            mediaPlayer.setMute(true);
            mediaPlayer.setVolume(volume);
        } else {
            mediaPlayer.setVolume(volumeSlider.getValue());
        }
        //播放器准备就绪执行播放
        mediaPlayer.setOnReady(() -> {
            mediaPlayer.play();
        });
        //给播放器添加当前播放时间的监听器，更新当前播放进度条的信息
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
                                Duration newValue) {
                if (!songSlider.isPressed()) {
                    songSlider.setValue(newValue.toSeconds());
                }
            }
        });
        //播放器到结束时执行的操作
        mediaPlayer.setOnEndOfMedia(() -> {
            switch (currentPlayMode) {
                case "单曲循环": {
                    mediaPlayer.seek(new Duration(0));  //定位到0毫秒(0秒)的时间，重新开始播放
                    mediaPlayer.play();
                    break;
                }
                case "顺序播放": {
                    //顺序播放模式下，如果歌曲表格只有一首歌，那就定位到0毫秒(0秒)的时间，等待下一次播放
                    if (tableSong.getItems().size() == 1) {
                        mediaPlayer.seek(new Duration(0));
                    }
                    //否则，歌曲表格大于1，顺序播放，直到最后一首歌播放结束后，释放media资源
                    else {
                        //如果下一首的索引在表格中，播放下一首
                        currentPlayIndex = currentPlayIndex + 1;
                        if (currentPlayIndex <= tableSong.getItems().size() - 1) {
                            this.mediaAutoPlay(tableSong.getItems().get(currentPlayIndex));
                        }
                        //否则，定位到歌曲表格第一首歌，等待下一次播放
                        else {
                            this.mediaDestroy();
                            this.playImageView.setImage(new Image("image/PlaybackPause.png"));  //设置暂停状态的图标
                            this.musicNameLabel.setText(tableSong.getItems().get(0).getMusicName());
                            this.labSinger.setText(tableSong.getItems().get(0).getSinger());
                            this.songSlider.setMax(tableSong.getItems().get(0).getTotalSeconds());
                            this.totalTimeLabel.setText(tableSong.getItems().get(0).getTotalTime());
                            media = new Media(new File(tableSong.getItems().get(0).getSrc()).toURI().toString());
                            mediaPlayer = new MediaPlayer(media);
                            mediaPlayer.setVolume(volumeSlider.getValue());
                        }
                    }
                    break;
                }
                case "列表循环": {
                    //列表循环模式下，如果歌曲表格只有一首歌，只要把mediaPlayer的当前播放时间重新设置为0秒就可以了
                    if (tableSong.getItems().size() == 1) {
                        mediaPlayer.seek(new Duration(0));  //定位到0毫秒(0秒)的时间，重新开始播放
                        mediaPlayer.play();
                    }
                    //否则，执行下一首歌曲播放，形成循环列表
                    else {
                        currentPlayIndex = currentPlayIndex + 1;
                        if (currentPlayIndex > tableSong.getItems().size() - 1) {  //如果当前索引越界，值为0，形成一个循环
                            currentPlayIndex = 0;
                        }
                        this.mediaAutoPlay(tableSong.getItems().get(currentPlayIndex));
                    }
                    break;
                }
                case "随机播放": {
                    //随机播放模式下，如果歌曲表格只有一首歌，只要把mediaPlayer的当前播放时间重新设置为0秒就可以了
                    if (tableSong.getItems().size() == 1) {
                        mediaPlayer.seek(new Duration(0));  //定位到0毫秒(0秒)的时间，重新开始播放
                        mediaPlayer.play();
                    }
                    //否则，歌曲表格大于1，生成一个非当前播放的索引值来播放
                    else {
                        lastPlayIndexList.add(currentPlayIndex);
                        //nextPlayIndexList的大小等0，证明当前没有需要播放下一首歌曲的索引，直接生成随机数播放
                        if (nextPlayIndexList.size() == 0) {
                            //先记录当前的索引是上一首需要的索引

                            //然后生成一个随机数不是当前播放的索引值，执行播放
                            while (true) {
                                int randomIndex = new Random().nextInt(tableSong.getItems().size());
                                if (randomIndex != currentPlayIndex) {
                                    currentPlayIndex = randomIndex;
                                    break;
                                }
                            }
                            this.mediaAutoPlay(tableSong.getItems().get(currentPlayIndex));
                        } else {
                            int index = nextPlayIndexList.size() - 1;
                            this.mediaAutoPlay(tableSong.getItems().get(nextPlayIndexList.get(index)));
                            currentPlayIndex = nextPlayIndexList.get(nextPlayIndexList.size() - 1);
                            nextPlayIndexList.remove(nextPlayIndexList.size() - 1);
                        }
                    }
                    break;
                }
                default:
            }
        });
    }*/
}
