CREATE TABLE `coupon`.`coupons`
(
    `id`                   BIGINT(20) NOT NULL AUTO_INCREMENT,
    `title`                VARCHAR(255) NOT NULL COMMENT 'Coupon Name',
    `coupon_type`          VARCHAR(255) NOT NULL COMMENT 'Coupon Type (First-come-first-served, etc.)',
    `total_quantity`       INT NULL COMMENT 'Maximum Issuable Quantity',
    `issued_quantity`      INT NOT NULL COMMENT 'Issued Quantity',
    `discount_amount`      INT NOT NULL COMMENT 'Discount Amount',
    `min_available_amount` INT NOT NULL COMMENT 'Minimum Usage Amount',
    `date_issue_start`     datetime(6) NOT NULL COMMENT 'Issue Start Date and Time',
    `date_issue_end`       datetime(6) NOT NULL COMMENT 'Issue End Date and Time',
    `date_created`         datetime(6) NOT NULL COMMENT 'Creation Date and Time',
    `date_updated`         datetime(6) NOT NULL COMMENT 'Update Date and Time',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT 'Coupon Policy';

CREATE TABLE `coupon`.`coupon_issues`
(
    `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
    `coupon_id`    BIGINT(20) NOT NULL COMMENT 'Coupon ID',
    `user_id`      BIGINT(20) NOT NULL COMMENT 'User ID',
    `date_issued`  datetime(6) NOT NULL COMMENT 'Issue Date and Time',
    `date_used`    datetime(6) NULL COMMENT 'Usage Date and Time',
    `date_created` datetime(6) NOT NULL COMMENT 'Creation Date and Time',
    `date_updated` datetime(6) NOT NULL COMMENT 'Update Date and Time',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT 'Coupon Issuance History';

