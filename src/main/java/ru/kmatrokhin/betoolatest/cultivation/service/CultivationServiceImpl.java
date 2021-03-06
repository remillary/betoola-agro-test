package ru.kmatrokhin.betoolatest.cultivation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.kmatrokhin.betoolatest.company.dao.CompanyRepository;
import ru.kmatrokhin.betoolatest.cultivation.dao.CultivationRepository;
import ru.kmatrokhin.betoolatest.cultivation.model.CultivationConverter;
import ru.kmatrokhin.betoolatest.exception.EntityNotFoundException;
import ru.kmatrokhin.betoolatest.exception.ErrorCode;
import ru.kmatrokhin.betoolatest.openapi.api.CultivationsApiDelegate;
import ru.kmatrokhin.betoolatest.openapi.model.CultivationDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CultivationServiceImpl implements CultivationsApiDelegate {
  private final CultivationRepository cultivationRepository;
  private final CultivationConverter cultivationConverter;
  private final CompanyRepository companyRepository;

  @Override
  public ResponseEntity<List<CultivationDTO>> companiesCultivationsList(String companyId, Optional<String> X_CORRELATION_ID, Optional<String> name) {
    final var companyUUID  = UUID.fromString(companyId);
    companyRepository.findByIdAndDeletedDateNull(companyUUID)
        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND, "Company not found"));
    final var cultivations = name.isPresent()
        ? cultivationRepository.searchByCompanyAndName(companyUUID, name.get())
        : cultivationRepository.findByCompany_Id(companyUUID);

    final var cultivationDTOs = cultivations.stream()
        .map(cultivationConverter::createDTOFromCultivation).collect(Collectors.toList());
    return ResponseEntity.ok(cultivationDTOs);
  }

  @Override
  public ResponseEntity<CultivationDTO> companiesOrganizationUnitsFieldsCultivationsCreate(String companyId, CultivationDTO cultivationDTO, Optional<String> X_CORRELATION_ID) {
    final var company = companyRepository.findByIdAndDeletedDateNull(UUID.fromString(companyId))
        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND, "Company not found"));

    final var cultivation = cultivationConverter.createCultivationFromDTO(cultivationDTO, company);
    final var saved = cultivationRepository.save(cultivation);
    return ResponseEntity.ok(cultivationConverter.createDTOFromCultivation(saved));
  }
}
