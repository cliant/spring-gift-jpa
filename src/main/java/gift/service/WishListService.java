package gift.service;

import gift.dto.WishListDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.WishList;
import gift.exception.CustomException;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishListRepository;
import gift.util.JwtUtil;
import jakarta.transaction.Transactional;

import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishListService {
    
    private WishListRepository wishListRepository;
    private MemberRepository memberRepository;
    private ProductRepository productRepository;

    private JwtUtil jwtUtil;

    public WishListService(WishListRepository wishListRepository, ProductRepository productRepository, JwtUtil jwtUtil) {
        this.wishListRepository = wishListRepository;
        this.productRepository = productRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public Page<WishListDto> findWishListById(String token, int page) {

        long memberId = (long)jwtUtil.extractAllClaims(token).get("id");

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("product.id"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return wishListRepository.findByMemberId(pageable, memberId).map(WishListDto::fromEntity);
    }

    @Transactional
    public void addWishList(String token, long productId) {

        long memberId = (long)jwtUtil.extractAllClaims(token).get("id");

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException("Member with id" + memberId + " not found", HttpStatus.NOT_FOUND));
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new CustomException("Product with id " +  productId + " not found", HttpStatus.NOT_FOUND));;
        wishListRepository.save(new WishList(member, product));

    }

    @Transactional
    public void deleteWishList(String token, long productId) {
        long memberId = (long)jwtUtil.extractAllClaims(token).get("id");
        wishListRepository.deleteById(wishListRepository.findIdByMemberIdAndProductId(memberId, productId));;
    }
}
