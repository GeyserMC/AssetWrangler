package org.geysermc.assetwrangler.components;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SoundPreview extends JPanel {
    public SoundPreview(File file, String relativePath) {
        this.setLayout(new FlowLayout());

        JButton open = new JButton("Open");
        open.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        this.add(open);

        StringBuilder label = new StringBuilder("<html>");
        label.append("Path: ");
        label.append(relativePath);

        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = inputStream.getFormat();
            clip.open(inputStream);
            clip.start();

            label.append("<br/>Format: ");
            label.append(format.toString());
            label.append("<br/>Length: ");
            long durationInMillis = (long) (1000 * inputStream.getFrameLength() / inputStream.getFormat().getFrameRate());
            label.append(
                    String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes(durationInMillis),
                            TimeUnit.MILLISECONDS.toSeconds(durationInMillis) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationInMillis))
                    )
            );
        } catch (UnsupportedAudioFileException | IOException ignored) {} catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

        label.append("</html>");

        JLabel jLabel = new JLabel(label.toString());
        this.add(jLabel);
    }
}
