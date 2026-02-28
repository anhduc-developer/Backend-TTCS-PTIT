package vn.hunter.job.controller;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hunter.job.domain.Company;
import vn.hunter.job.domain.response.ResultPaginationDTO;
import vn.hunter.job.service.CompanyService;
import vn.hunter.job.util.annotation.ApiMessage;

import java.util.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(@Filter Specification<Company> spect,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.companyService.getAllCompanies(spect, pageable));
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Optional<Company>> getCompanyById(@PathVariable("id") Long id) {
        Optional<Company> company = this.companyService.getCompanyById(id);
        return ResponseEntity.ok().body(company);
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createNewCompaney(@Valid @RequestBody Company company) {
        Company newCompany = this.companyService.createCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<Company> updateCompany(@Valid @PathVariable("id") Long id,
            @RequestBody Company updateCompany) {
        Company newCompany = this.companyService.updateCompany(updateCompany, id);
        return ResponseEntity.ok().body(newCompany);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable("id") Long id) {
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok().body("Success!");
    }
}
