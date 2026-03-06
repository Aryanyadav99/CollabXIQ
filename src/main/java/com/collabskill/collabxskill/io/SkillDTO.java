package com.collabskill.collabxskill.io;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO {
    private String skillId;
    private String skillName;
    private String proficiencyLevel;
    private int yearsOfExperience;
}

