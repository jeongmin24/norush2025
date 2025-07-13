package com.capstone.norush2025.service;

import com.capstone.norush2025.code.ErrorCode;
import com.capstone.norush2025.domain.FavoriteRoute;
import com.capstone.norush2025.domain.user.User;
import com.capstone.norush2025.dto.request.FavoriteRouteAddRequest;
import com.capstone.norush2025.dto.request.FavoriteRouteUpdateRequest;
import com.capstone.norush2025.dto.response.FavoriteRouteResponse;
import com.capstone.norush2025.exception.BusinessLogicException;
import com.capstone.norush2025.repository.FavoriteRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.capstone.norush2025.code.ErrorCode.FAVORITE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FavoriteRouteService {

    private final FavoriteRouteRepository favoriteRouteRepository;
    private final UserService userService;

    @Transactional
    public FavoriteRouteResponse.FavoriteRouteInfo addFavoriteRoute(String userId, FavoriteRouteAddRequest request){

        validateDuplicateFavorite(userId,request.getName());

        User user = userService.getUser(userId);

        FavoriteRoute newFavorite = FavoriteRoute.builder()
                .userId(user.getUserId())
                .name(request.getName())
                .type(request.getType())
                .routeId(request.getRouteId())
                .memo(request.getMemo())
                .build();


        FavoriteRoute savedFavorite = favoriteRouteRepository.save(newFavorite);
        return new FavoriteRouteResponse.FavoriteRouteInfo(savedFavorite);
    }

    @Transactional(readOnly = true)
    public List<FavoriteRouteResponse.FavoriteRouteInfo> getFavoriteRoutes(String userId) {

        User user = userService.getUser(userId);

        return favoriteRouteRepository.findByUserId(user.getUserId()).stream()
                .map(FavoriteRouteResponse.FavoriteRouteInfo::new)
                .collect(Collectors.toList());
    }

    /**
     * 특정 유저의 특정 즐겨찾기
     * */
    @Transactional(readOnly = true)
    public FavoriteRouteResponse.FavoriteRouteInfo getFavoriteRouteByUserId(String favoriteRouteId, String userId){

        User user = userService.getUser(userId);

        FavoriteRoute favoriteRoute = findFavoriteRouteByFavoriteRouteIdAndUserId(favoriteRouteId, user.getUserId());

        return new FavoriteRouteResponse.FavoriteRouteInfo(favoriteRoute);
    }

    @Transactional
    public FavoriteRouteResponse.FavoriteRouteInfo updateFavoriteRoute(String favoriteRouteId, String userId, FavoriteRouteUpdateRequest request){

        User user = userService.getUser(userId);
        FavoriteRoute existingFavoriteRoute = findFavoriteRouteByFavoriteRouteIdAndUserId(favoriteRouteId, user.getUserId());

        existingFavoriteRoute.update(
                request.getName(),
                request.getType(),
                request.getRouteId(),
                request.getMemo()
        );

        FavoriteRoute updatedFavoriteRoute = favoriteRouteRepository.save(existingFavoriteRoute);
        return new FavoriteRouteResponse.FavoriteRouteInfo(updatedFavoriteRoute);
    }

    @Transactional
    public void deleteFavoriteRoute(String favoriteId, String userId) {

        User user = userService.getUser(userId);

        FavoriteRoute favoriteToDelete = findFavoriteRouteByFavoriteRouteIdAndUserId(favoriteId, user.getUserId());

        favoriteRouteRepository.delete(favoriteToDelete);
    }


    /**
     * userId, name(즐겨찾기시 붙이는 이름)으로 즐겨찾기 중복 검사
     * 동일한 사용자가 같은 이름("출근길")으로 두번 저장하는걸 막기 위한 유효성 검사
     * */
    private void validateDuplicateFavorite(String userId, String favoriteRouteName) {
        if (favoriteRouteRepository.findByUserIdAndName(userId, favoriteRouteName).isPresent()) {
            throw new BusinessLogicException(ErrorCode.FAVORITE_ALREADY_EXISTS.getMessage());
        }
    }


    private FavoriteRoute findFavoriteRouteByFavoriteRouteIdAndUserId(String favoriteId, String userId) {
        return favoriteRouteRepository.findByFavoriteRouteIdAndUserId(favoriteId, userId)
                .orElseThrow(() -> new BusinessLogicException(FAVORITE_NOT_FOUND.getMessage()));
    }
}
