package org.github.felipegutierrez.explore.akka.classic.remote.counter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MasterAppJ {

    public MasterAppJ() throws InterruptedException, IOException {
        Config config = ConfigFactory.parseString("akka.remote.artery.canonical.port = 2551")
                .withFallback(ConfigFactory.load("remote/remoteActorsWordCount.conf"));
        ActorSystem system = ActorSystem.create("MasterSystem", config);
        ActorRef master = system.actorOf(WordCountMasterJ.props(), "wordCountMaster");

        master.tell(new Initialize(5), master);
        Thread.sleep(2000);

        InputStream inputStream = getFileFromResourceAsStream("txt/hamlet.txt");

        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        for (String line; (line = reader.readLine()) != null; ) {
            // Process line
            master.tell(line, ActorRef.noSender());
        }
    }

    // public static void main(String[] args) throws IOException, InterruptedException {
    public static void run(String[] args) throws IOException, InterruptedException {
        new MasterAppJ();
    }

    private InputStream getFileFromResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }
}
