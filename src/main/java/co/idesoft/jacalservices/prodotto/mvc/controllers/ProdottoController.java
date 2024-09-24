package co.idesoft.jacalservices.prodotto.mvc.controllers;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.idesoft.jacalservices.prodotto.mvc.controllers.dto.AggiornareProdottoDto;
import co.idesoft.jacalservices.prodotto.mvc.controllers.dto.CreareProdottoDto;
import co.idesoft.jacalservices.prodotto.mvc.controllers.dto.ProdottoCreatoDto;
import co.idesoft.jacalservices.prodotto.mvc.controllers.dto.ProdottoItemDto;
import co.idesoft.jacalservices.prodotto.mvc.entities.Prodotto;
import co.idesoft.jacalservices.prodotto.mvc.repositories.ProdottoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("api/prodotti")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ProdottoController {

    private final ProdottoRepository prodottoRepository;

    @GetMapping
    public ResponseEntity<Page<ProdottoItemDto>> getAllProdotti(Pageable pageable) {
        // List<ProdottoItemDto> prodotti = prodottoRepository.findAll()
        // .stream()
        // .map(ProdottoItemDto::fromEntity)
        // .collect(Collectors.toList());

        Page<ProdottoItemDto> prodottiPage = prodottoRepository.findAll(pageable)
                .map(ProdottoItemDto::fromEntity);

        return ResponseEntity.ok(prodottiPage);
    }

    @PostMapping
    public ResponseEntity<ProdottoCreatoDto> creareProdotto(@Valid @RequestBody CreareProdottoDto request) {
        log.info("creando un nuovo prodotto con request: {}", request);

        Long prodottoId = prodottoRepository.save(Prodotto.from(request))
                .getProdottoId();

        return new ResponseEntity<>(new ProdottoCreatoDto(prodottoId), HttpStatus.CREATED);
    }

    @PutMapping("{prodottoId}")
    public ResponseEntity<Void> updateProdotto(@PathVariable Long prodottoId,
            @RequestBody AggiornareProdottoDto request) {
        log.info("aggiornando il prodotto con id: {}", prodottoId);
        Optional<Prodotto> prodotto = this.prodottoRepository.findById(prodottoId);

        if (prodotto.isPresent()) {
            Prodotto prodottoAModificare = prodotto.get();
            prodottoAModificare.aggiornaCon(request);

            this.prodottoRepository.save(prodottoAModificare);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("{prodottoId}")
    public ResponseEntity<Void> cancellaProdotto(@PathVariable Long prodottoId) {
        log.info("cancellando il prodotto con id: {}", prodottoId);
        this.prodottoRepository.deleteById(prodottoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}