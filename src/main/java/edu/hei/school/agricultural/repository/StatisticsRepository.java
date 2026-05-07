package edu.hei.school.agricultural.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StatisticsRepository {

    private final Connection connection;

    public Map<String, Double> getEarnedAmountByMember(String collectivityId, LocalDate from, LocalDate to) {
        Map<String, Double> result = new HashMap<>();
        String sql = """
                SELECT mp.member_debited_id, COALESCE(SUM(mp.amount), 0) AS earned_amount
                FROM member_payment mp
                JOIN membership_fee mf ON mp.membership_fee_id = mf.id
                JOIN collectivity_member cm ON mp.member_debited_id = cm.member_id
                WHERE cm.collectivity_id = ?
                  AND mf.collectivity_id = ?
                  AND mp.creation_date >= ?
                  AND mp.creation_date <= ?
                GROUP BY mp.member_debited_id
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ps.setString(2, collectivityId);
            ps.setDate(3, java.sql.Date.valueOf(from));
            ps.setDate(4, java.sql.Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("member_debited_id"), rs.getDouble("earned_amount"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Map<String, Double> getUnpaidAmountByMember(String collectivityId, LocalDate from, LocalDate to) {
        Map<String, Double> result = new HashMap<>();
        double totalDue = getActiveFeesTotalForCollectivity(collectivityId, from, to);
        Map<String, Double> earned = getEarnedAmountByMember(collectivityId, from, to);
        Map<String, Double> allMembers = getAllMemberIdsForCollectivity(collectivityId);

        for (String memberId : allMembers.keySet()) {
            double paid = earned.getOrDefault(memberId, 0.0);
            double unpaid = Math.max(0.0, totalDue - paid);
            result.put(memberId, unpaid);
        }
        return result;
    }

    public Map<String, Double> getAllMemberIdsForCollectivity(String collectivityId) {
        Map<String, Double> result = new HashMap<>();
        String sql = "SELECT member_id FROM collectivity_member WHERE collectivity_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("member_id"), 0.0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Map<String, Integer> getNewMembersCountByCollectivity(LocalDate from, LocalDate to) {
        Map<String, Integer> result = new HashMap<>();
        String sql = """
                SELECT cm.collectivity_id, COUNT(DISTINCT cm.member_id) AS new_members
                FROM collectivity_member cm
                WHERE cm.member_id IN (
                    SELECT DISTINCT mp.member_debited_id
                    FROM member_payment mp
                    WHERE mp.creation_date >= ?
                      AND mp.creation_date <= ?
                    GROUP BY mp.member_debited_id
                    HAVING MIN(mp.creation_date) >= ?
                )
                GROUP BY cm.collectivity_id
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(from));
            ps.setDate(2, java.sql.Date.valueOf(to));
            ps.setDate(3, java.sql.Date.valueOf(from));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("collectivity_id"), rs.getInt("new_members"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Map<String, Double> getMembersUpToDatePercentageByCollectivity(LocalDate from, LocalDate to) {
        Map<String, Double> result = new HashMap<>();
        List<String> collectivityIds = getAllCollectivityIds();

        for (String collectivityId : collectivityIds) {
            double totalDue = getActiveFeesTotalForCollectivity(collectivityId, from, to);
            Map<String, Double> earned = getEarnedAmountByMember(collectivityId, from, to);
            Map<String, Double> allMembers = getAllMemberIdsForCollectivity(collectivityId);

            if (allMembers.isEmpty()) {
                result.put(collectivityId, 100.0);
                continue;
            }

            long upToDateCount = allMembers.keySet().stream()
                    .filter(memberId -> earned.getOrDefault(memberId, 0.0) >= totalDue)
                    .count();

            double percentage = (double) upToDateCount / allMembers.size() * 100.0;
            result.put(collectivityId, Math.round(percentage * 100.0) / 100.0);
        }
        return result;
    }

    private double getActiveFeesTotalForCollectivity(String collectivityId, LocalDate from, LocalDate to) {
        String sql = """
                SELECT COALESCE(SUM(amount), 0) AS total
                FROM membership_fee
                WHERE collectivity_id = ?
                  AND status = 'ACTIVE'
                  AND eligible_from >= ?
                  AND eligible_from <= ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ps.setDate(2, java.sql.Date.valueOf(from));
            ps.setDate(3, java.sql.Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    private List<String> getAllCollectivityIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT id FROM collectivity";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) ids.add(rs.getString("id"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ids;
    }
}