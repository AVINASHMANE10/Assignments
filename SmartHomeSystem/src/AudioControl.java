package src;

interface AudioControl {
    void adjustVolume(int level);
    void mute();
    boolean isMuted();
    int getVolume();
}
