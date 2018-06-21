package interfaces;

public interface NoteStateChangeListener {

    void onChange(int position , int noteID , int state , Boolean success);
    void onStart();
    void onFinish();
}
