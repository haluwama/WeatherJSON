import org.glassfish.json.JsonPrettyGeneratorImpl;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.stream.JsonGenerator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Window extends JFrame {

    private JTextField textFieldApiCode, textFieldCity;
    private JTextArea msgBody;
    private JButton connectingButton;
    public Window() {
        setTitle("WeatherJSON");
        setSize(450, 600);
        setResizable(false);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;

        setLocation(x,y);

        JPanel northPanel = new JPanel();
        northPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridLayout gridLayout2Per2 = new GridLayout(2, 2);
        northPanel.setLayout(gridLayout2Per2);
        JLabel apiCodeLabel = new JLabel("Enter a API code:");
        textFieldApiCode = new JTextField("621848fa7b67d9e7c8997f47bba7c41e");

        northPanel.add(apiCodeLabel);
        northPanel.add(textFieldApiCode);

        JLabel labelCity = new JLabel("Enter a city name:");
        textFieldCity = new JTextField("Warszawa");

        northPanel.add(labelCity);
        northPanel.add(textFieldCity);

        add(northPanel, BorderLayout.PAGE_START);

        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridLayout gridLayout2Per1 = new GridLayout(3, 1);
        centerPanel.setLayout(gridLayout2Per1);

        msgBody = new JTextArea();
        add(msgBody, BorderLayout.CENTER);

        connectingButton = new JButton("Connect");
        add(connectingButton, BorderLayout.SOUTH);
        connectingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                msgBody.setText("");

                String urlText = "https://api.openweathermap.org/data/2.5/weather?q=" + textFieldCity.getText() + "&appid=" + textFieldApiCode.getText() + "&units=metric";
                try {
                    URL url = new URL(urlText);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(url.openStream()));

                    String content = "", inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        content += inputLine;
                    }

                    content = content.replaceAll(",", ",\n");
                    msgBody.setText(content);

                    InputStream inputStream = new ByteArrayInputStream(content.getBytes());
                    Map<String, Object> map = new HashMap<>();
                    map.put(JsonGenerator.PRETTY_PRINTING, true);
                    JsonReaderFactory readerFactory = Json.createReaderFactory(Collections.emptyMap());
                    JsonReader jsonReader = readerFactory.createReader(inputStream);
                    JsonObject jsonObject = jsonReader.readObject();

                    double temp = jsonObject.getJsonObject("main").getJsonNumber("temp").doubleValue();
                    String msg = "";

                    msg += "The temperature in " + textFieldCity.getText() + " is: " + jsonObject.getJsonObject("main").getJsonNumber("temp").doubleValue() + "\n";

                    if (temp > 25) {
                        msg += "You should wear summer clothes. It's warm outside!\n";
                    } else if(temp <= 25 && temp > 20) {
                        msg += "It's quite warm outside.\n";
                    } else if(temp <= 20 && temp > 10) {
                        msg += "You should consider wearing trousers or jeans.\n";
                    } else {
                        msg += "It's cold. You must wear a jacket.\n";
                    }

                    msg += "The humidity: " + jsonObject.getJsonObject("main").getJsonNumber("humidity").doubleValue() + "\n";

                    JOptionPane.showMessageDialog(null, msg, "What should you wear?", 1);

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    JOptionPane.showMessageDialog(null, "The typed city can't be found.", "Error!", 0);
                }
            }
        });
    }
}
