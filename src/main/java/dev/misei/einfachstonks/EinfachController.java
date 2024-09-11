package dev.misei.einfachstonks;

//import dev.misei.einfachstonks.neuralservice.network.NeuralNetworkService;
import dev.misei.einfachstonks.stonkservice.StonkService;
import dev.misei.einfachstonks.stonkservice.model.ETFBridgeType;
import dev.misei.einfachstonks.stonkservice.model.ETFType;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
@AllArgsConstructor
public class EinfachController {


    //private NeuralNetworkService neuralNetworkService;
    private StonkService stonkService;

    @GetMapping
    public ResponseEntity<String> createTracker(String etfName, ETFBridgeType etfBridgeType, String ticker, ETFType etfType) {
        return ResponseEntity.ok(stonkService.createETFTracker(etfName, etfBridgeType, ticker, etfType).toString());
    }

    @GetMapping
    public ResponseEntity<String> trackAndFeed(String etfName, ETFBridgeType etfBridgeType, String ticker, ETFType etfType) {
        return ResponseEntity.ok(stonkService.createETFTracker(etfName, etfBridgeType, ticker, etfType).toString());
    }

}
