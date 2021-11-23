select
       dom_first_text(dom, '.itemInfo-wrap .sku-name') as title,
       str_substring_after(dom_first_text(dom, '.p-parameter .parameter2 li:contains(商品名称)'), '：') as productName,
       dom_first_text(dom, '.p-parameter .parameter2 li:contains(商品名称)') as productNameRaw,
       dom_first_text(dom, '.p-parameter #parameter-brand a') as brand,
       str_substring_after(dom_first_text(dom, '.p-parameter .parameter2 li:contains(货号)'), '：') as model,
       dom_first_text(dom, '#choose-attrs #choose-attr-1 div.item.selected a i') as specification,
       dom_first_float(dom, '.itemInfo-wrap .p-price .price', 0.0) as price,
       dom_first_text(dom, '.itemInfo-wrap .p-price .price') as priceText,
       dom_first_attr(dom, '.choose-amount input', 'value') as minNumberToBuy,
       dom_first_attr(dom, '.choose-amount input', 'data-max') as maxNumberToBuy,   -1 as salesVolume,   -1 as shopScore,   dom_first_text(dom, '.contact .name a[href]') as supplier,   dom_first_href(dom, '.contact .name a') as supplierUrl,   'unknown' as deliveryFrom,   'unknown' as supplierTel,   dom_first_float(dom, '.itemInfo-wrap #comment-count a', 0.0) as commentCount,   dom_first_text(dom, '.itemInfo-wrap #comment-count a') as commentCountText,   dom_first_text(dom, '.itemInfo-wrap #summary-quan .quan-item') as coupon,   dom_first_attr(dom, '.itemInfo-wrap #summary-quan .quan-item', 'title') as couponComment,   dom_first_text(dom, '.itemInfo-wrap #summary-promotion') as promotion,   dom_first_text(dom, '.itemInfo-wrap #summary-service a') as deliveryBy,   dom_first_text(dom, '.itemInfo-wrap .services') as services,   dom_all_texts(dom, '.itemInfo-wrap #choose-attrs #choose-attr-1 a') as variants,   dom_base_uri(dom) as baseUri
from
    load_and_select('{{url}}', 'body');
