package org.lpro.geoquizz.boundary;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.lpro.geoquizz.Exception.NotFound;
import org.lpro.geoquizz.entity.Partie;
import org.lpro.geoquizz.entity.Photo;
import org.lpro.geoquizz.entity.Serie;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping(value = "/series", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Serie.class)
public class SerieRepresentation {

    private final SerieResource sr;
    private final PhotoResource pr;
    private final PartieResource par;

    public SerieRepresentation(SerieResource sr, PhotoResource pr, PartieResource par) {
        this.sr = sr;
        this.pr = pr;
        this.par = par;
    }

    @GetMapping
    public ResponseEntity<?> getAllPhotos() {
        return new ResponseEntity<>(sr.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> postSerie(@RequestBody Serie serie) {
        serie.setId(UUID.randomUUID().toString());
        Serie saved = sr.save(serie);
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/{ID}")
    public ResponseEntity<?> getSerieById(@PathVariable("ID") String id) {
        return Optional.ofNullable(sr.findById(id))
                .filter(Optional::isPresent)
                .map(serie -> new ResponseEntity<>(serie.get(),HttpStatus.OK))
                .orElseThrow( () -> new NotFound("Serie inexistante"));
    }

    @GetMapping("/{ID}/photos")
    public ResponseEntity<?> getPhotoBySerieId(@PathVariable("ID") String id) throws NotFound {
        if (!sr.existsById(id)){
            throw new NotFound("Serie inexistante");
        }
        return new ResponseEntity<>(pr.findBySerieId(id), HttpStatus.OK);
    }

    @PostMapping("/{ID}/photos")
    public ResponseEntity<?> ajoutPhoto(@PathVariable("ID") String id, @RequestBody Photo photo) throws NotFound {
        return sr.findById(id)
                .map(serie -> {
                    photo.setId(UUID.randomUUID().toString());
                    photo.setSerie(serie);
                    photo.setUrl("http://6377e1c0.ngrok.io/images/"+ photo.getId());
                    pr.save(photo);
                    return new ResponseEntity<>(photo.getId(),HttpStatus.CREATED);
                }).orElseThrow( () -> new NotFound("Photo inexistante"));
    }

    @GetMapping("/{ID}/parties")
    public ResponseEntity<?> getPartieBySerieId(@PathVariable("ID") String id) throws NotFound {
        if (!sr.existsById(id)){
            throw new NotFound("Serie inexistante");
        }
        return new ResponseEntity<>(par.findBySerieId(id), HttpStatus.OK);
    }

    @PostMapping("/{ID}/parties")
    public ResponseEntity<?> ajoutPartie(@PathVariable("ID") String id, @RequestBody Partie partie) throws NotFound {
        return sr.findById(id)
                .map(serie -> {
                    partie.setId(UUID.randomUUID().toString());
                    String jwtToken = Jwts.builder().setSubject(partie.getId()).claim("roles", "serie").setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "secretkey").compact();
                    partie.setSerie(serie);
                    partie.setToken(jwtToken);
                    partie.setScore(0);
                    partie.setStatus("En cours");

                    Set<Photo> photos = new HashSet<Photo>();
                    for (int i = 0; i < 10; i++) {
                        Photo photo = pr.find1random().get(0);
                        photos.add(photo);
                    }
                    partie.setPhotos(photos);
                    par.save(partie);
                    return new ResponseEntity<>(partie,HttpStatus.CREATED);
                }).orElseThrow( () -> new NotFound("Serie inexistante"));
    }
}