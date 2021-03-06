package org.lpro.geoquizzplayer.boundary;

import org.lpro.geoquizzplayer.entity.Partie;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="geoquizz-back")
@RibbonClient(name="geoquizz-back")
@RequestMapping(value = "/series", produces = MediaType.APPLICATION_JSON_VALUE)
public interface SerieRepresentation {

    @GetMapping
    ResponseEntity<?> getSeries();

    @PostMapping("/{ID}/parties")
    ResponseEntity<?> ajoutPartie(@PathVariable("ID") String id, @RequestBody Partie partie);
}