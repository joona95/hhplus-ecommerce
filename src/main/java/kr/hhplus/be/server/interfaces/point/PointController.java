package kr.hhplus.be.server.interfaces.point;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.common.auth.AuthUser;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static kr.hhplus.be.server.interfaces.point.PointResponse.*;

@RestController
@RequestMapping("/api/v1/points")
public class PointController implements PointApiSpec {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @GetMapping
    @Override
    public ResponseEntity<UserPointResponse> getUserPoint(@AuthUser User user) {

        UserPointResponse response = UserPointResponse.from(pointService.findByUser(user));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/charge")
    @Override
    public ResponseEntity<UserPointResponse> charge(@AuthUser User user, @RequestBody @Valid PointRequest.PointChargeRequest request) {

        UserPointResponse response = UserPointResponse.from(pointService.charge(user, request.toCommand()));

        return ResponseEntity.ok(response);
    }
}
