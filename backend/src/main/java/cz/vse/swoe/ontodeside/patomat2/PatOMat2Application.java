package cz.vse.swoe.ontodeside.patomat2;

import cz.vse.swoe.ontodeside.patomat2.util.NLPPipelineProvider;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PatOMat2Application {

    public static void main(String[] args) {
        SpringApplication.run(PatOMat2Application.class, args);
    }

    @PostConstruct
    public void initNLPPipeline() {
        // Init the NLP pipeline on startup as it takes some time
        NLPPipelineProvider.initNLPPipeline();
    }
}
