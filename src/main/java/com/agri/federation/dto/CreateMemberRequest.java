package com.agri.federation.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import com.agri.federation.model.Gender;
import com.agri.federation.model.MemberOccupation;

@Data
public class CreateMemberRequest {

    // MemberInformation
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String profession;
    private String phoneNumber;
    private String email;
    private MemberOccupation occupation;

    // Création seulement
    private Long collectivityIdentifier;
    private List<Long> referees;
    private boolean registrationFeePaid;
    private boolean membershipDuesPaid;
}