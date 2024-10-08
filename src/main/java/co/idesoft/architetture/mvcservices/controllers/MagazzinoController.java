package co.idesoft.architetture.mvcservices.controllers;

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

import co.idesoft.architetture.mvcservices.controllers.dto.AggiornareMagazzinoDto;
import co.idesoft.architetture.mvcservices.controllers.dto.CreareMagazzinoDto;
import co.idesoft.architetture.mvcservices.controllers.dto.MagazzinoCreatoDto;
import co.idesoft.architetture.mvcservices.controllers.dto.MagazzinoDettaglioDto;
import co.idesoft.architetture.mvcservices.controllers.dto.MagazzinoItemDto;
import co.idesoft.architetture.mvcservices.entities.Magazzino;
import co.idesoft.architetture.mvcservices.exceptions.ConflictException;
import co.idesoft.architetture.mvcservices.exceptions.RecordNotFoundException;
import co.idesoft.architetture.mvcservices.services.MagazzinoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/magazzini")
@RequiredArgsConstructor
@Slf4j
public class MagazzinoController {

    private final MagazzinoService magazzinoService;

    @GetMapping
    public ResponseEntity<Page<MagazzinoItemDto>> getAllMagazzini(Pageable pageable, String q) {
        Page<MagazzinoItemDto> magazziniPage = magazzinoService.findAll(pageable, q)
                .map(MagazzinoItemDto::fromEntity);
        return ResponseEntity.ok(magazziniPage);
    }

    @PostMapping
    public ResponseEntity<MagazzinoCreatoDto> creareMagazzino(@RequestBody CreareMagazzinoDto request) {
        log.info("creando un nuovo magazzino con request: {}", request);

        try {
            Long magazzinoId = magazzinoService.save(request);
            return new ResponseEntity<>(new MagazzinoCreatoDto(magazzinoId), HttpStatus.CREATED);
        } catch (ConflictException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }

    @GetMapping("{magazzinoId}")
    public ResponseEntity<MagazzinoDettaglioDto> getDettaglioMagazzino(@PathVariable Long magazzinoId) {
        Optional<Magazzino> magazzino = magazzinoService.findDettaglio(magazzinoId);
        if (magazzino.isPresent()) {
            MagazzinoDettaglioDto magazzinoDettaglioDto = MagazzinoDettaglioDto.fromEntity(magazzino.get());

            return new ResponseEntity<>(magazzinoDettaglioDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("{magazzinoId}")
    public ResponseEntity<Void> updateMagazzino(@PathVariable Long magazzinoId,
            @RequestBody AggiornareMagazzinoDto request) {
        try {
            magazzinoService.update(magazzinoId, request);
        } catch (ConflictException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (RecordNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("{magazzinoId}")
    public ResponseEntity<Void> cancellaMagazzino(@PathVariable Long magazzinoId) {
        magazzinoService.cancella(magazzinoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
