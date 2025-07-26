CREATE TABLE users(
	id bigint IDENTITY(1, 1) NOT NULL,
	username varchar(255) null,
	email varchar(255) NULL,
	password varchar(255) null,
	first_name varchar(255) null,
	middle_name varchar(255) null,
	last_name varchar(255) null,
	phone_number varchar(255) null,
	address varchar(2000) null,
	zip_code varchar(20) null,
	mfa_key varchar(255) null,
	mfa_enabled bit default 0 NOT null,
	mfa_enforced bit default 0 NOT null,
	invited_by varchar(255) null,
	invite_status varchar(255) null,
	invite_link varchar(1000) null,
	invite_code varchar(255) null,
	invite_sent_on datetime null,
	invite_accepted_on datetime null,
	temp_password varchar(255) null,
	login_count int null,
	last_login_at datetime null,
	profile_image_location varchar(255) null,
	profile_image_access_uri varchar(255) null,
	profile_image_file_name varchar(255) null,
	enabled bit default 0 NOT NULL,
	account_locked bit default 0 NOT NULL,
	account_expired bit default 0 NOT null,
	credentials_expired bit default 0 NOT null,
	client_ip varchar(255) null,
	session_id varchar(255) null,
	owner_id bigint null,
	is_default bit default 0 NOT NULL,
	created_by varchar(255) null,
	created_on datetime null,
	updated_by varchar(255) null,
	updated_on datetime null,
	CONSTRAINT users_pkey PRIMARY KEY (id)
);
ALTER TABLE users ADD CONSTRAINT unk_username unique (username);
ALTER TABLE users ADD CONSTRAINT unk_email unique (email);

CREATE TABLE roles(
	id bigint IDENTITY(1, 1) NOT NULL,
	name varchar(255) null,
	description varchar(255) NULL,
	created_by varchar(255) null,
    created_on datetime null,
    updated_by varchar(255) null,
    updated_on datetime null,
    is_default bit default 0 NOT NULL,
	CONSTRAINT roles_pkey PRIMARY KEY (id)
);
ALTER TABLE roles ADD CONSTRAINT unk_role_name unique (name);

CREATE TABLE users_roles(
	user_id bigint NOT NULL,
	role_id bigint not NULL
);
ALTER TABLE users_roles ADD CONSTRAINT fk_users_users_roles_user_id FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE users_roles ADD CONSTRAINT fk_roles_users_roles_role_id FOREIGN KEY (role_id) REFERENCES roles(id);

CREATE TABLE permissions(
	id bigint IDENTITY(1, 1) NOT NULL,
	category varchar(255) not null,
    name varchar(255) not null,
	created_by varchar(255) null,
    created_on datetime null,
    updated_by varchar(255) null,
    updated_on datetime null,
    CONSTRAINT permissions_pkey PRIMARY KEY (id)
);

CREATE TABLE roles_permissions(
	role_id bigint not NULL,
	permission_id bigint NOT NULL
);
ALTER TABLE roles_permissions ADD CONSTRAINT fk_roles_roles_permissions_role_id FOREIGN KEY (role_id) REFERENCES roles(id);
ALTER TABLE roles_permissions ADD CONSTRAINT fk_permissions_roles_permissions_permission_id FOREIGN KEY (permission_id) REFERENCES permissions(id);

CREATE TABLE preferences(
	id bigint IDENTITY(1, 1) NOT NULL,
	category varchar(255) not null,
    pf_key varchar(255) not null,
    pf_value varchar(255) not null,
	created_by varchar(255) null,
    created_on datetime null,
    updated_by varchar(255) null,
    updated_on datetime null,
    CONSTRAINT preferences_pkey PRIMARY KEY (id)
);
ALTER TABLE preferences ADD CONSTRAINT unk_cat_pfkey_pfvalue unique (category, pf_key, pf_value);

create table customers(
	id bigint IDENTITY(1, 1) NOT NULL,
    uid varchar(255) null,
	name varchar(255) null,
	company_name varchar(255) null,
	company_abrv varchar(255) null,
	phone_no varchar(255) null,
	email varchar(255) null,
	address varchar(500) null,
	city varchar(100) null,
	zip_code varchar(20) null,
	country varchar(255) null,
	state varchar(255) null,
	favourite bit default 0  null,
	is_new bit default 0  null,
	is_lead bit default 0 null,
	status varchar(20) null,
	profile_image_location varchar(255) null,
    profile_image_access_uri varchar(255) null,
    profile_image_file_name varchar(255) null,
	created_by varchar(255) null,
    created_on datetime null,
    updated_by varchar(255) null,
    updated_on datetime null
    CONSTRAINT customers_pkey PRIMARY KEY (id)
);

-- drop table products
create table products(
	id bigint IDENTITY(1, 1) NOT NULL,
    uid varchar(255) not null,
    name varchar(255) not null,
	cust_prd_number varchar(255) null,
	category varchar(255) null,
	type varchar(255) null,
	capsule_size varchar(255) null,
	packaging_type varchar(255) null,
	count_per_bottle int null,
	batch_size int null,
	dosage_per_unit int null,
	dynamic_fields nvarchar(max) null,
	material_cost DECIMAL(10, 2) null ,
	process_loss DECIMAL(10, 2) null ,
	filler_cost DECIMAL(10, 2) null ,
	freight_charges DECIMAL(10, 2) null ,
	mark_up DECIMAL(10, 2) null ,
	capsule_filling_cost DECIMAL(10, 2) null ,
	packaging_cost DECIMAL(10, 2) null ,
	testing_cost DECIMAL(10, 2) null ,
	stability_cost DECIMAL(10, 2) null ,
	fulfillment_cost DECIMAL(10, 2) null ,
	created_by varchar(255) null,
    created_on datetime null,
    updated_by varchar(255) null,
    updated_on datetime null,
    CONSTRAINT products_pkey PRIMARY KEY (id)
);

-- drop table quotations
create table quotations(
	id bigint IDENTITY(1, 1) NOT NULL,
    uid varchar(255) null,
	qt_number varchar(255) not null,
	qt_owner varchar(255) null,
	status varchar(255) not null,
	qt_date date null,
	qt_expiry_date date null,
	dynamic_fields nvarchar(max) null,
	created_by varchar(255) null,
    created_on datetime null,
    updated_by varchar(255) null,
    updated_on datetime null,
    comments varchar(5000) null,
    customer_id bigint null,
    product_id bigint null,
    version int default 1,
    CONSTRAINT quotations_pkey PRIMARY KEY (id)
);
ALTER TABLE quotations ADD CONSTRAINT fk_quotations_customers_customer_id FOREIGN KEY (customer_id) REFERENCES customers(id);
ALTER TABLE quotations ADD CONSTRAINT fk_quotations_products_product_id FOREIGN KEY (product_id) REFERENCES products(id);

create table product_ingredients(
	id bigint IDENTITY(1, 1) NOT NULL,
    rm_id varchar(255) null,
	active_ingredient varchar(255) not null,
	label varchar(255) null,
	units varchar(255) null,
	per_dosage varchar(255) null,
	qty_unit varchar(255) null,
	price_per_unit DECIMAL(10, 2) null,
	cost DECIMAL(10, 2) null ,
	created_by varchar(255) null,
    created_on datetime null,
    updated_by varchar(255) null,
    updated_on datetime null,
    product_id bigint null,
    CONSTRAINT product_ingredients_pkey PRIMARY KEY (id)
);
ALTER TABLE product_ingredients ADD CONSTRAINT fk_product_ingredients_products_product_id FOREIGN KEY (product_id) REFERENCES products(id);

create table documents(
	id bigint IDENTITY(1, 1) NOT NULL,
    uid varchar(255) not null,
    source nvarchar(max) not null,
    local_file_path varchar(255) not null,
	file_name varchar(255) null,
	file_type varchar(255) null,
	template_id varchar(255) null,
	backup_type varchar(255) null, -- aws s3 etc..
	backup_location varchar(255) null, -- aws s3 bucket name
	backup_access_url varchar(255) null, -- aws s3 bucket uri
	backup_on datetime null,
	created_by varchar(255) null,
    created_on datetime null,
    updated_by varchar(255) null,
    updated_on datetime null,
    CONSTRAINT documents_pkey PRIMARY KEY (id)
);
create table quotations_audit_history(
	id bigint IDENTITY(1, 1) NOT NULL,
	quotation_id bigint null,
	uid varchar(255) null,
	qt_number varchar(255) not null,
	qt_owner varchar(255) null,
	status varchar(255) not null,
	qt_date date null,
	qt_expiry_date date null,
	dynamic_fields nvarchar(max) null,
	submitted_by varchar(255) null,
    submitted_on datetime null,
    comments varchar(5000) null,
    audit_comments varchar(5000) null,
    customer_id bigint null,
    product_id bigint null,
    version int default 1,
    operation varchar(10),
    changes nvarchar(max) null,
    CONSTRAINT quotations_history_pkey PRIMARY KEY (id)
);
ALTER TABLE quotations_audit_history ADD CONSTRAINT fk_quotations_audit_history_quotations_quotation_id FOREIGN KEY (quotation_id) REFERENCES quotations(id);


create table customers_audit_history(
	id bigint IDENTITY(1, 1) NOT NULL,
	customer_id bigint null,
	uid varchar(255) null,
	name varchar(255) not null,
	company_name varchar(255) null,
	company_abrv varchar(255) null,
	phone_no varchar(255) null,
	email varchar(255) null,
	address varchar(500) null,
	city varchar(100) null,
	zip_code varchar(20) null,
	country varchar(255) null,
	state varchar(255) null,
	favourite bit default 0  null,
	is_new bit default 0  null,
	is_lead bit default 0 null,
	notes varchar(5000) null,
	status varchar(20) null,
	profile_image_location varchar(255) null,
    profile_image_access_uri varchar(255) null,
    profile_image_file_name varchar(255) null,
	submitted_by varchar(255) null,
    submitted_on datetime null,
    operation varchar(10),
    changes nvarchar(max) null,
    CONSTRAINT customers_audit_history_pkey PRIMARY KEY (id)
);
ALTER TABLE customers_audit_history ADD CONSTRAINT fk_customers_audit_history_customers_customer_id FOREIGN KEY (customer_id) REFERENCES customers(id);

create table product_packaging_details (
	id bigint IDENTITY(1, 1) NOT NULL primary key,
    packaging_type varchar(255),
    details nvarchar(max),
    created_by varchar(255) null,
    created_on datetime null,
    updated_by varchar(255) null,
    updated_on datetime null,
    product_id bigint foreign key references products(id),
);

create table countries (
	id bigint IDENTITY(1, 1) NOT NULL primary key,
	name varchar(255),
	iso_two_char_code char(2),
	iso_three_char_code char(3),
	currency_code varchar(3),
	currency_symbol varchar(8),
	isd_code varchar(25),
	created_by varchar(255) ,
    created_on datetime ,
    updated_by varchar(255) ,
    updated_on datetime
);
CREATE TABLE states (
	id bigint IDENTITY(1, 1) NOT NULL primary key,
    state_code VARCHAR(2) ,
    state_name VARCHAR(50),
    country_id bigint foreign key references countries(id)
);
create table customer_notes(
	id bigint IDENTITY(1, 1) NOT NULL primary key,
	notes varchar(5000),
	created_by varchar(255) ,
    created_on datetime ,
    updated_by varchar(255) ,
    updated_on datetime,
	customer_id bigint foreign key references customers(id)
);
