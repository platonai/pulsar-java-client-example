-- noinspection SqlDialectInspectionForFile
-- noinspection SqlResolveForFile
-- noinspection SqlNoDataSourceInspectionForFile

select
       '京东' as site,
       dom_first_text(dom, '.itemInfo-wrap .sku-name') as product_title,
       str_substring_after(dom_first_text(dom, '.p-parameter .parameter2 li:contains(商品名称)'), '：') as product_name,
       dom_first_img(dom, '.product-intro img') as big_img_url,
       dom_first_text(dom, '.itemInfo-wrap .sku-name') as product_title,
       dom_first_text(dom, '.p-parameter #parameter-brand a') as brand,
       str_substring_after(dom_first_text(dom, '.p-parameter .parameter2 li:contains(货号)'), '：') as model,
       dom_first_text(dom, '#choose-attrs #choose-attr-1 div.item.selected a i') as specification,
       str_substring_after(dom_first_text(dom, '.p-parameter .parameter2 li:contains(材质)'), '：') as material,
       dom_first_float(dom, '.itemInfo-wrap .p-price .price', 0.0) as price,
       dom_first_text(dom, '.itemInfo-wrap .p-price .price') as price_text,
       dom_first_attr(dom, '.choose-amount input', 'value') as min_amount_to_buy,
       dom_first_attr(dom, '.choose-amount input', 'data-max') as max_amount_to_buy,
       -1 as sales_volume,
       -1 as inventory_amount,
       dom_first_text(dom, '.detail .comment-percent .percent-con') as favorable_rate,
       str_substring_between(dom_first_text(dom, '.detail .comments-list li a:contains(好评)'), '(', ')') as good_reviews,
       str_substring_between(dom_first_text(dom, '.detail .comments-list li a:contains(中评)'), '(', ')') as normal_reviews,
       str_substring_between(dom_first_text(dom, '.detail .comments-list li a:contains(差评)'), '(', ')') as bad_reviews,

       dom_first_text(dom, '.crumb-wrap .contact .name a[href]') as shop_name,
       dom_first_href(dom, '.crumb-wrap .contact .name a') as shop_url,
       'unknown' as shop_location,
       'unknown' as shop_tel,
       dom_first_attr(dom, '.crumb-wrap .contact .star .star-gray', 'title') as shop_scores,
       'unknown' as express_fee,

       dom_first_float(dom, '.itemInfo-wrap #comment-count a', 0.0) as commentCount,
       dom_first_text(dom, '.itemInfo-wrap #comment-count a') as commentCountText,
       dom_first_text(dom, '.itemInfo-wrap #summary-quan .quan-item') as coupon,
       dom_first_attr(dom, '.itemInfo-wrap #summary-quan .quan-item', 'title') as couponComment,
       dom_first_text(dom, '.itemInfo-wrap #summary-promotion') as promotion,
       dom_first_text(dom, '.itemInfo-wrap #summary-service a') as deliveryBy,
       dom_first_text(dom, '.itemInfo-wrap .services') as services,
       dom_all_texts(dom, '.itemInfo-wrap #choose-attrs #choose-attr-1 a') as variants,
       dom_base_uri(dom) as baseUri
from
    load_and_select('{{url}}', 'body');
