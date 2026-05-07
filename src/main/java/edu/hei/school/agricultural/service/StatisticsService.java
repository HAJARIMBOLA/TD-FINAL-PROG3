package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.*;
import edu.hei.school.agricultural.entity.Collectivity;
import edu.hei.school.agricultural.entity.Member;
import edu.hei.school.agricultural.exception.NotFoundException;
import edu.hei.school.agricultural.repository.CollectivityRepository;
import edu.hei.school.agricultural.repository.MemberRepository;
import edu.hei.school.agricultural.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;
    private final StatisticsRepository statisticsRepository;

    public List<CollectivityLocalStatistics> getLocalStatistics(String collectivityId, LocalDate from, LocalDate to) {
        Collectivity collectivity = collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity.id=" + collectivityId + " not found"));

        List<Member> members = memberRepository.findAllByCollectivity(collectivity);

        Map<String, Double> earnedByMember = statisticsRepository.getEarnedAmountByMember(collectivityId, from, to);
        Map<String, Double> unpaidByMember = statisticsRepository.getUnpaidAmountByMember(collectivityId, from, to);

        List<CollectivityLocalStatistics> stats = new ArrayList<>();
        for (Member member : members) {
            MemberDescription memberDescription = MemberDescription.builder()
                    .id(member.getId())
                    .firstName(member.getFirstName())
                    .lastName(member.getLastName())
                    .email(member.getEmail())
                    .occupation(member.getOccupation() != null ? member.getOccupation().name() : null)
                    .build();

            stats.add(CollectivityLocalStatistics.builder()
                    .memberDescription(memberDescription)
                    .earnedAmount(earnedByMember.getOrDefault(member.getId(), 0.0))
                    .unpaidAmount(unpaidByMember.getOrDefault(member.getId(), 0.0))
                    .build());
        }
        return stats;
    }

    public List<CollectivityOverallStatistics> getOverallStatistics(LocalDate from, LocalDate to) {
        Map<String, Double> percentageByCollectivity = statisticsRepository.getMembersUpToDatePercentageByCollectivity(from, to);
        Map<String, Integer> newMembersByCollectivity = statisticsRepository.getNewMembersCountByCollectivity(from, to);

        List<CollectivityOverallStatistics> result = new ArrayList<>();
        for (String collectivityId : percentageByCollectivity.keySet()) {
            collectivityRepository.findById(collectivityId).ifPresent(collectivity -> {
                CollectivityInformation info = new CollectivityInformation(collectivity.getName(), collectivity.getNumber());
                result.add(CollectivityOverallStatistics.builder()
                        .collectivityInformation(info)
                        .overallMemberCurrentDuePercentage(percentageByCollectivity.getOrDefault(collectivityId, 0.0))
                        .newMembersNumber(newMembersByCollectivity.getOrDefault(collectivityId, 0))
                        .build());
            });
        }
        return result;
    }
}