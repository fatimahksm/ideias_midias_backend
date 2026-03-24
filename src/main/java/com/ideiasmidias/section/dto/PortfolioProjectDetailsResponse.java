package com.ideiasmidias.section.dto;

import com.ideiasmidias.portfolio.dto.PortfolioProjectMediaResponse;
import com.ideiasmidias.portfolio.dto.PortfolioProjectResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PortfolioProjectDetailsResponse {

    private PortfolioProjectResponse project;
    private List<PortfolioProjectMediaResponse> media;
}
