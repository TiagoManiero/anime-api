package com.tiagomaniero.essentials.controller;

import com.tiagomaniero.essentials.domain.Anime;
import com.tiagomaniero.essentials.requests.AnimePostRequestBody;
import com.tiagomaniero.essentials.requests.AnimePutRequestBody;
import com.tiagomaniero.essentials.service.AnimeService;
import com.tiagomaniero.essentials.util.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/animes")
@Log4j2
@AllArgsConstructor
public class AnimeController {

    private final DateUtil dateUtil;
    private final AnimeService animeService;

    @GetMapping
    @Operation(
            summary = "List all animes paginated",
            description = "The default size is 20, parameter size change default value",
            tags = {"anime"}
    )
    public ResponseEntity<Page<Anime>> list(@ParameterObject Pageable pageable){
        log.info(dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.listAll(pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Anime>> listAll(){
        log.info(dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.listAllNonPageable());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Anime> findById(@PathVariable(value = "id") Long id){
        return ResponseEntity.ok(animeService.findByIdorThrowBadRequest(id));
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Anime> findByIdAuthentication(@PathVariable(value = "id") Long id,
                                                        @AuthenticationPrincipal UserDetails userDetails){

        log.info(userDetails);
        return ResponseEntity.ok(animeService.findByIdorThrowBadRequest(id));
    }

    @GetMapping("/find")
    public ResponseEntity<List<Anime>> findByName(@RequestParam String name){
        return ResponseEntity.ok(animeService.findByName(name));
    }

    @PostMapping
    public ResponseEntity<Anime> save(@RequestBody @Valid AnimePostRequestBody animePostRequestBody){
        return new ResponseEntity<>(animeService.save(animePostRequestBody), HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful Operation"),
            @ApiResponse(responseCode = "400", description = "When Anime does not exists the database")
    })
    public ResponseEntity<Void> delete(@PathVariable(value = "id") Long id){
        animeService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Void> replace(@RequestBody AnimePutRequestBody animePutRequestBody){
        animeService.replace(animePutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
