package org.geysermc.assetwrangler.components.previews;

import lombok.SneakyThrows;
import org.geysermc.assetwrangler.components.ClosableComponent;
import org.geysermc.assetwrangler.utils.ImmutableBoundedRangeModel;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

// Adapted from https://litux.nl/mirror/javaexamples/0596006209_jenut3-chp-17-sect-4.html
public class SoundPreview extends JPanel implements ClosableComponent {
    private final Clip clip;
    private final AudioInputStream stream;

    private final JSlider progressBar;
    private final JButton playButton;
    private final JLabel timeLabel;
    private final Timer timer;

    private final int audioLength;
    private int audioPosition = 0;

    @SneakyThrows
    public SoundPreview(File file, String relativePath) {
        this.setLayout(new FlowLayout());

        StringBuilder label = new StringBuilder("<html>");
        label.append("Path: ");
        label.append(relativePath);

        clip = AudioSystem.getClip();
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = inputStream.getFormat();

        // convert stream for playback (https://web.archive.org/web/20200220131053/http://www.javazoom.net/vorbisspi/documents.html)
        AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(),
                16, format.getChannels(), format.getChannels() * 2, format.getSampleRate(), false);
        stream = AudioSystem.getAudioInputStream(targetFormat, inputStream);

        clip.open(stream);

        audioLength = (int)(clip.getMicrosecondLength( )/1000);

        label.append("<br/>Format: ");
        label.append(format);
        label.append("<br/>Length: ");
        label.append(audioLength / 1000);
        label.append(".");
        label.append((audioLength % 1000) / 100);
        label.append("</html>");

        timeLabel = new JLabel("0.0");
        this.add(timeLabel);

        progressBar = new JSlider(SwingConstants.HORIZONTAL);
        progressBar.setModel(new ImmutableBoundedRangeModel(0, audioLength));
        progressBar.addChangeListener(e -> {
            int value = progressBar.getValue();
            timeLabel.setText(value/1000 + "." + (value%1000)/100);

            if (value != audioPosition) skip(value);
        });
        this.add(progressBar);

        playButton = new JButton("Play");
        playButton.addActionListener(e -> {
            if (playButton.getText().equals("Play")) {
                play();
            } else {
                stop();
            }
        });
        this.add(playButton);

        JLabel jLabel = new JLabel(label.toString());
        this.add(jLabel);

        timer = new Timer(10, e -> {
            sliderUpdate();
        });
    }

    public void play() {
        clip.start();
        timer.start();
        playButton.setText("Stop");
    }

    public void stop() {
        timer.stop();
        clip.stop();
        playButton.setText("Play");
    }

    public void reset() {
        stop();
        clip.setMicrosecondPosition(0);
        audioPosition = 0;
        progressBar.setValue(0);
    }

    public void skip(int position) {
        if (position < 0 || position > audioLength) return;
        audioPosition = position;
        clip.setMicrosecondPosition(position * 1000L);
        progressBar.setValue(position);
    }

    private void sliderUpdate() {
        if (clip.isActive()) {
            audioPosition = (int)(clip.getMicrosecondPosition( )/1000);
            progressBar.setValue(audioPosition);
        } else {
            reset();
        }
    }

    @Override
    public void close() {
        clip.close();
        timer.stop();
        try {
            stream.close();
        } catch (IOException ignored) {}
    }
}
