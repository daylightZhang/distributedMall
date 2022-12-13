package com.sample.mall.goods.service.impl;


import com.sample.mall.common.base.ResponseEnum;
import com.sample.mall.common.dto.GoodsDTO;
import com.sample.mall.common.dto.OrderItemDTO;
import com.sample.mall.common.exception.BusinessException;
import com.sample.mall.common.util.Assert;
import com.sample.mall.common.util.ObjectTransformer;
import com.sample.mall.goods.mapper.GoodsMapper;
import com.sample.mall.goods.model.GoodsDO;
import com.sample.mall.goods.service.IGoodsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoodsServiceImpl implements IGoodsService {

    @Resource
    GoodsMapper goodsMapper;

    @Override
    public boolean addGoods(GoodsDTO goodsDTO) {
        GoodsDO goodsDO = ObjectTransformer.transform(goodsDTO, GoodsDO.class);
        int result = goodsMapper.insertGoods(goodsDO);
        return Assert.singleRowAffected(result);
    }

    @Override
    public GoodsDTO getGoods(Long id) {
        GoodsDO goodsDO = goodsMapper.selectGoodsById(id);
        Assert.notNull(goodsDO);
        return ObjectTransformer.transform(goodsDO, GoodsDTO.class);
    }

    /**
     * 扣减商品库存
     *
     * <p>
     * update t_goods
     *    set stock = stock - #{number}
     *  where id = #{goodsId}
     *    and stock >= #{number};
     * </p>
     *
     * 重点：stock >= #{number} <br/>
     * 最后增加这个条件，既可以做到不需要提前锁库存，也可以起到防止超卖的作用
     *
     * @param orderItemDTOList
     * @return
     */
    @Override
    public boolean decreaseStock(List<OrderItemDTO> orderItemDTOList) {
        GoodsDO goodsDO;
        List<GoodsDO> goodsDOList = new ArrayList<>();
        for (OrderItemDTO orderItem : orderItemDTOList) {
            goodsDO = new GoodsDO();
            goodsDO.setId(orderItem.getGoodsId());
            // 这里使用stock字段，作为被扣减库存
            goodsDO.setStock(orderItem.getNumber());
            goodsDOList.add(goodsDO);
        }
        int result = goodsMapper.updateGoodsListStock(goodsDOList);
        if (result != goodsDOList.size()) {
            // 扣减成功的数量，与订单明细数量不一致，说明库存扣减有问题
            throw new BusinessException(ResponseEnum.GOODS_STOCK_NOT_ENOUGH);
        }
        return true;
    }

}
