package site.bzyl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeDto {
    private Integer id;
    private String name;
    private String username;
    private String phone;
    private String sex;
}