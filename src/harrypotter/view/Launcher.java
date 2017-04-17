package harrypotter.view;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Launcher extends JFrame{
	public Launcher() throws LineUnavailableException, IOException, UnsupportedAudioFileException{
		setTitle("HarryPotter");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
	}
	public void addAudio() throws LineUnavailableException, IOException, UnsupportedAudioFileException
	{
		File m = new File("HarryPotterMusic.wav");
		Clip clip = AudioSystem.getClip();
		clip.open(AudioSystem.getAudioInputStream(m));
		clip.loop(clip.LOOP_CONTINUOUSLY);
		clip.start();
	}
	public void addPanel(JPanel p){
		this.add(p);
	}

}
