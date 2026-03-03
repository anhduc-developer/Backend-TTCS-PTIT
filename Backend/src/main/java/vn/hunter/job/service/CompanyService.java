package vn.hunter.job.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hunter.job.domain.Company;
import vn.hunter.job.domain.User;
import vn.hunter.job.domain.response.ResultPaginationDTO;
import vn.hunter.job.repository.CompanyRepository;
import vn.hunter.job.repository.UserRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public ResultPaginationDTO getAllCompanies(Specification<Company> spect, Pageable pageable) {
        Page<Company> pageUser = this.companyRepository.findAll(spect, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageUser.getNumber() + 1);
        mt.setPageSize(pageUser.getSize());
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(pageUser.getContent());
        return rs;

    }

    public Optional<Company> getCompanyById(Long id) {
        return this.companyRepository.findById(id);
    }

    public Company createCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public Company updateCompany(Company updateCompany, Long id) {
        return this.companyRepository.findById(id).map(company -> {
            company.setName(updateCompany.getName());
            company.setAddress(updateCompany.getAddress());
            company.setDescription(updateCompany.getDescription());
            company.setLogo(updateCompany.getLogo());
            return this.companyRepository.save(company);
        }).orElseThrow(() -> new NoSuchElementException("Company not found"));
    }

    public void deleteCompany(Long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            Company company = companyOptional.get();
            List<User> users = this.userRepository.findByCompany(company);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }
}
