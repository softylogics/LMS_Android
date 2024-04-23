package com.dusre.lms;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerControlView;

import com.dusre.lms.Util.Constants;
import com.dusre.lms.databinding.FragmentPlayerLayoutBinding;
import com.dusre.lms.listeners.SetOnClickListener;
import com.dusre.lms.viewmodel.CoursesViewModel;
import com.dusre.lms.viewmodel.DownloadedVideoViewModel;
import com.dusre.lms.viewmodel.LessonsViewModel;
import com.dusre.lms.viewmodel.SectionsViewModel;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.PlayerUiController;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoPlayerActivity extends AppCompatActivity implements SetOnClickListener {

    private FragmentPlayerLayoutBinding binding;


//    private List<Lesson> lessonList;
    private LessonsViewModel lessonsViewModel;

    private DownloadedVideoViewModel downloadedVideoViewModel;

    private ExoPlayer player;
//    private NextVideoAdapter adapter;
    private boolean playWhenReady = true;
    private boolean shouldRepeat = false;
    private boolean isFullscreen = false;
    private boolean isLocked = false;

    @OptIn(markerClass = UnstableApi.class) @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentPlayerLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        CoursesViewModel coursesViewModel = new ViewModelProvider(this).get(CoursesViewModel.class);
        SectionsViewModel sectionsViewModel = new ViewModelProvider(this).get(SectionsViewModel.class);
        lessonsViewModel = new ViewModelProvider(this).get(LessonsViewModel.class);
        downloadedVideoViewModel = new ViewModelProvider(this).get(DownloadedVideoViewModel.class);
        initializePlayer();
//        binding..setLayoutManager(new LinearLayoutManager(getContext()));
//       if(!Constants.isDownloadVideoPlay) {
//           adapter = new NextVideoAdapter(getContext(), lessonsViewModel, this);
//           binding.recyclerViewNextVideos.setAdapter(adapter);
//       }

        binding.playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null && player.isPlaying()) {
                    pauseVideo();
                } else {
                    playVideo();
                }
            }
        });
        binding.repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null) {
                    shouldRepeat = !shouldRepeat;
                    setRepeatMode(player);

                }
            }
        });
        binding.fullscreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null) {
//            isFullscreen = !isFullscreen;
//            setFullscreenMode(player);
                    if(isFullscreen) {


                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                        if(getSupportActionBar() != null){
                            getSupportActionBar().show();
                        }

                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.playerView.getLayoutParams();
                        params.width = params.MATCH_PARENT;
                        params.height = (int) ( 200 * getApplicationContext().getResources().getDisplayMetrics().density);
                        binding.playerView.setLayoutParams(params);

                        isFullscreen = false;
                    }else{


                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                        if(getSupportActionBar() != null){
                            getSupportActionBar().hide();
                        }

                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.playerView.getLayoutParams();
                        params.width = params.MATCH_PARENT;
                        params.height = params.MATCH_PARENT;
                        binding.playerView.setLayoutParams(params);

                        isFullscreen = true;
                    }
                }
            }

        });

        binding.speedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.speed_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.speed_0_25x) {
                        setPlaybackSpeed(0.25f);
                        return true;
                    } else if (itemId == R.id.speed_0_5x) {
                        setPlaybackSpeed(0.5f);
                        return true;
                    } else if (itemId == R.id.speed_normal) {
                        setPlaybackSpeed(1f);
                        return true;
                    } else if (itemId == R.id.speed_1_5x) {
                        setPlaybackSpeed(1.5f);
                        return true;
                    } else if (itemId == R.id.speed_2x) {
                        setPlaybackSpeed(2f);
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            }
        });
        binding.playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                setControllerVisibility(visibility);
            }
        });

//binding.lockBtn.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//        isLocked = !isLocked;
//        setLockMode();
//    }
//});

        binding.playerView.setControllerShowTimeoutMs(2000);




    }

//    @OptIn(markerClass = UnstableApi.class)
//    public View onCreate(@NonNull LayoutInflater inflater,
//                                                                     ViewGroup container, Bundle savedInstanceState) {
//
//
//        binding = FragmentPlayerLayoutBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        coursesViewModel = new ViewModelProvider(this).get(CoursesViewModel.class);
//        sectionsViewModel = new ViewModelProvider(this).get(SectionsViewModel.class);
//        lessonsViewModel = new ViewModelProvider(this).get(LessonsViewModel.class);
//        downloadedVideoViewModel = new ViewModelProvider(this).get(DownloadedVideoViewModel.class);
//        initializePlayer();
////        binding.recyclerViewNextVideos.setLayoutManager(new LinearLayoutManager(getContext()));
////       if(!Constants.isDownloadVideoPlay) {
////           adapter = new NextVideoAdapter(getContext(), lessonsViewModel, this);
////           binding.recyclerViewNextVideos.setAdapter(adapter);
////       }
//
//        binding.playPauseBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (player != null && player.isPlaying()) {
//                    pauseVideo();
//                } else {
//                    playVideo();
//                }
//            }
//        });
//        binding.repeatBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (player != null) {
//                    shouldRepeat = !shouldRepeat;
//                    setRepeatMode(player);
//
//                }
//            }
//        });
//binding.fullscreenBtn.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//        if (player != null) {
////            isFullscreen = !isFullscreen;
////            setFullscreenMode(player);
//            if(isFullscreen) {
//
//
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//
//                if(getActionBar()!= null){
//                    getActionBar().show();
//                }
//
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.playerView.getLayoutParams();
//                params.width = params.MATCH_PARENT;
//                params.height = (int) ( 200 * getApplicationContext().getResources().getDisplayMetrics().density);
//                binding.playerView.setLayoutParams(params);
//
//                isFullscreen = false;
//            }else{
//
//
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
//                        |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//
//                if(getActionBar() != null){
//                    getActionBar().hide();
//                }
//
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//
//                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.playerView.getLayoutParams();
//                params.width = params.MATCH_PARENT;
//                params.height = params.MATCH_PARENT;
//                binding.playerView.setLayoutParams(params);
//
//                isFullscreen = true;
//            }
//        }
//        }
//
//});
//
//binding.speedBtn.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View view) {
//        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
//        popupMenu.getMenuInflater().inflate(R.menu.speed_menu, popupMenu.getMenu());
//        popupMenu.setOnMenuItemClickListener(item -> {
//            int itemId = item.getItemId();
//            if (itemId == R.id.speed_0_25x) {
//                setPlaybackSpeed(0.25f);
//                return true;
//            } else if (itemId == R.id.speed_0_5x) {
//                setPlaybackSpeed(0.5f);
//                return true;
//            } else if (itemId == R.id.speed_normal) {
//                setPlaybackSpeed(1f);
//                return true;
//            } else if (itemId == R.id.speed_1_5x) {
//                setPlaybackSpeed(1.5f);
//                return true;
//            } else if (itemId == R.id.speed_2x) {
//                setPlaybackSpeed(2f);
//                return true;
//            }
//            return false;
//        });
//        popupMenu.show();
//    }
//});
//binding.playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
//    @Override
//    public void onVisibilityChange(int visibility) {
//        setControllerVisibility(visibility);
//    }
//});
//
////binding.lockBtn.setOnClickListener(new View.OnClickListener() {
////    @Override
////    public void onClick(View v) {
////        isLocked = !isLocked;
////        setLockMode();
////    }
////});
//
//binding.playerView.setControllerShowTimeoutMs(2000);
//
//
//
//
//
//        return root;
//    }

    private void setPlaybackSpeed(float speed) {
        if (player != null) {
            PlaybackParameters playbackParameters = new PlaybackParameters(speed);
            player.setPlaybackParameters(playbackParameters);
        }
    }

    private void setControllerVisibility(int visibility) {
        binding.topController.setVisibility(visibility);
//        binding.bottomController.setVisibility(visibility);
        binding.playPauseBtn.setVisibility(visibility);
//        if (!isLocked)
//            binding.lockBtn.setVisibility(visibility);
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        player.release();
//        binding = null;
//    }
    @OptIn(markerClass = UnstableApi.class) private void setLockMode() {
        if (isLocked) {
            binding.playerView.setUseController(false);
            binding.playerView.hideController();
//            binding.lockBtn.setImageResource(R.drawable.ic_lock_open);
        } else {
            binding.playerView.setUseController(true);
            binding.playerView.showController();
//            binding.lockBtn.setImageResource(R.drawable.ic_lock);
        }
    }
    private void playVideo() {
        binding.playPauseBtn.setImageResource(R.drawable.ic_pause);
        if (player != null) {
            player.play();
        }
    }
    private void pauseVideo() {
        binding.playPauseBtn.setImageResource(R.drawable.ic_play);
        if (player != null) {
            player.pause();
        }
    }
    private void setRepeatMode(ExoPlayer player) {
        if (player == null) return;
        if (shouldRepeat) {
            player.setRepeatMode(Player.REPEAT_MODE_ONE);
            binding.repeatBtn.setImageResource(androidx.media3.ui.R.drawable.exo_legacy_controls_repeat_one);
        } else {
            player.setRepeatMode(Player.REPEAT_MODE_OFF);
            binding.repeatBtn.setImageResource(androidx.media3.ui.R.drawable.exo_legacy_controls_repeat_off);
        }
    }
    @OptIn(markerClass = UnstableApi.class) private void setFullscreenMode(ExoPlayer player) {
        if (player == null) return;
        if (isFullscreen) {
            binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            binding.fullscreenBtn.setImageResource(androidx.media3.ui.R.drawable.exo_ic_fullscreen_exit);
        } else {
            binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            binding.fullscreenBtn.setImageResource(androidx.media3.ui.R.drawable.exo_ic_fullscreen_enter);
        }
    }
    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onItemClickCourse(int position) {

    }

    @Override
    public void onItemClickCourseSection(int position) {

    }

    @Override
    public void onCheckBoxClick(int position) {

    }

    @Override
    public void onDownloadButtonClick(int position) {

    }

    @Override
    public void onLessonNameClick(int position) {




    }

    @Override
    public void onNextLessonClick(int position) {
        Constants.current_lesson_id = position;
        MediaItem mediaItem = MediaItem.fromUri(lessonsViewModel.getMyLessons().getValue().get(position).getVideo_url_web());
// Set the media item to be played.
        player.setMediaItem(mediaItem);
// Prepare the player.
        player.prepare();
// Start the playback.
        player.play();
    }

    @Override
    public void onDownloadedItemClickCourse(int position) {

    }

    @Override
    public void onDownloadedLessonNameClick(int position) {

    }

    @Override
    public void onDownloadedNextLessonClick(int position) {

    }

    @Override
    public void onDownloadDeleteVideo(int position) {

    }

    private void initializePlayer() {
        if (player != null) {
            player.release();
        }
        if(Constants.isDownloadVideoPlay){
            binding.youtubePlayerView.setVisibility(View.GONE);
            binding.playerView.setVisibility(View.VISIBLE);
            binding.topController.setVisibility(View.VISIBLE);

            player = new ExoPlayer.Builder(this).build();
            // Bind the player to the view.
            binding.playerView.setPlayer(player);
            // Build the media item.
            String folderName = "DownloadedVideos";
            String path = Constants.downloadedLessons.get(Constants.current_downloaded_lesson_id).getVideoPath();
            Log.d("downloadPlayer" , path);
            String name = path.substring(path.lastIndexOf("/")+1);
            //            File file = new File(getActivity().getFilesDir(), name);
//            File directory = getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

// Create a File object representing the downloaded file
//            File downloadedFile = new File(directory, name);
////            String uri = downloadedFile.getAbsolutePath();
//            Uri uri = FileProvider.getUriForFile(getContext(), "com.dusre.lms.fileprovider", downloadedFile);
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(path));
// Set the media item to be played.
            player.setMediaItem(mediaItem);
            player.setPlayWhenReady(playWhenReady);
            setRepeatMode(player);
            setFullscreenMode(player);

// Prepare the player.
            player.prepare();
            binding.videoTitle.setText(name);

// Start the playback.
            if (playWhenReady) {
                playVideo();
            } else {
                pauseVideo();
            }
        }
        else {

            if(Constants.lessons.get(Constants.current_lesson_id).getVideo_url_web().contains("youtube")){
                binding.youtubePlayerView.setVisibility(View.VISIBLE);
                binding.playerView.setVisibility(View.GONE);
                binding.topController.setVisibility(View.GONE);
                getLifecycle().addObserver(binding.youtubePlayerView);
                YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        DefaultPlayerUiController defaultPlayerUiController = new DefaultPlayerUiController(binding.youtubePlayerView, youTubePlayer);
                        defaultPlayerUiController.showVideoTitle(true);
                        defaultPlayerUiController.showMenuButton(true);
                        defaultPlayerUiController.setMenuButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                                popupMenu.getMenuInflater().inflate(R.menu.speed_menu, popupMenu.getMenu());
                                popupMenu.setOnMenuItemClickListener(item -> {
                                    int itemId = item.getItemId();
                                    if (itemId == R.id.speed_0_25x) {
                                        youTubePlayer.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_0_25);
                                        return true;
                                    } else if (itemId == R.id.speed_0_5x) {
                                        youTubePlayer.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_0_5);
                                        return true;
                                    } else if (itemId == R.id.speed_normal) {
                                        youTubePlayer.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_1);
                                        return true;
                                    } else if (itemId == R.id.speed_1_5x) {
                                        youTubePlayer.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_1_5);
                                        return true;
                                    } else if (itemId == R.id.speed_2x) {
                                        youTubePlayer.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_2);
                                        return true;
                                    }
                                    return false;
                                });
                                    popupMenu.show();

                            }
                        });

//                        defaultPlayerUiController.showCustomAction1(true);
//                        Drawable icon = getDrawable(R.drawable.playback_speed);
//                        icon.setTint(getResources().getColor(R.color.myWindowBackground));
//                        float dpScale = getResources().getDisplayMetrics().density;
//                        int iconSizePx = (int) (16 * dpScale + 0.5f); // Convert dp to pixels
//
//                        // Set the bounds (width and height) of the drawable
//                        icon.setBounds(0, 0, iconSizePx, iconSizePx);
//                        defaultPlayerUiController.setCustomAction1(icon, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
//                                popupMenu.getMenuInflater().inflate(R.menu.speed_menu, popupMenu.getMenu());
//                                popupMenu.setOnMenuItemClickListener(item -> {
//                                    int itemId = item.getItemId();
//                                    if (itemId == R.id.speed_0_25x) {
//                                        youTubePlayer.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_0_25);
//                                        return true;
//                                    } else if (itemId == R.id.speed_0_5x) {
//                                        youTubePlayer.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_0_5);
//                                        return true;
//                                    } else if (itemId == R.id.speed_normal) {
//                                        youTubePlayer.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_1);
//                                        return true;
//                                    } else if (itemId == R.id.speed_1_5x) {
//                                        youTubePlayer.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_1_5);
//                                        return true;
//                                    } else if (itemId == R.id.speed_2x) {
//                                        youTubePlayer.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_2);
//                                        return true;
//                                    }
//                                    return false;
//                                });
//                                popupMenu.show();
//                            }
//                        });
                        defaultPlayerUiController.setFullscreenButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if(isFullscreen) {


                                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                                    if(getSupportActionBar() != null){
                                        getSupportActionBar().show();
                                    }

                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.youtubePlayerView.getLayoutParams();
                                    params.width = params.MATCH_PARENT;
                                    params.height = (int) ( 200 * getApplicationContext().getResources().getDisplayMetrics().density);
                                    binding.youtubePlayerView.setLayoutParams(params);

                                    isFullscreen = false;
                                }else{


                                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                                            |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                            |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                                    if(getSupportActionBar() != null){
                                        getSupportActionBar().hide();
                                    }

                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.youtubePlayerView.getLayoutParams();
                                    params.width = params.MATCH_PARENT;
                                    params.height = params.MATCH_PARENT;
                                    binding.youtubePlayerView.setLayoutParams(params);

                                    isFullscreen = true;
                                }
                            }
                        });
                        defaultPlayerUiController.showYouTubeButton(false);
                        binding.youtubePlayerView.setCustomPlayerUi(defaultPlayerUiController.getRootView());
                        youTubePlayer.loadVideo(extractVideoID(), 0);
                    }

                };

                IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).build();
                binding.youtubePlayerView.initialize(listener, options);

            }
            else{
                binding.youtubePlayerView.setVisibility(View.GONE);
                binding.playerView.setVisibility(View.VISIBLE);
                binding.topController.setVisibility(View.VISIBLE);
                player = new ExoPlayer.Builder(this).build();
                // Bind the player to the view.
                binding.playerView.setPlayer(player);
                // Build the media item.
                MediaItem mediaItem = MediaItem.fromUri(Constants.lessons.get(Constants.current_lesson_id).getVideo_url_web());
    // Set the media item to be played.
                player.setMediaItem(mediaItem);
                player.setPlayWhenReady(playWhenReady);
                setRepeatMode(player);
                setFullscreenMode(player);

    // Prepare the player.
                player.prepare();
                binding.videoTitle.setText(Constants.lessons.get(Constants.current_lesson_id).getTitle());
    // Start the playback.
                if (playWhenReady) {
                    playVideo();
                } else {
                    pauseVideo();
                }
            }
        }
    }

    private String extractVideoID() {
        String videoId = null;
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2F)[^#\\&\\?\\n]*";

        try {
            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(Constants.lessons.get(Constants.current_lesson_id).getVideo_url_web());

            if (matcher.find()) {
                videoId = matcher.group();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return videoId;


    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onBackPressed() {

//            isFullscreen = !isFullscreen;
//            setFullscreenMode(player);
            if (isFullscreen) {
                if (player != null) {

                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                if (getSupportActionBar() != null) {
                    getSupportActionBar().show();
                }

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.playerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
                binding.playerView.setLayoutParams(params);

                isFullscreen = false;
            }
        }
        else {
            super.onBackPressed();
        }

    }
}