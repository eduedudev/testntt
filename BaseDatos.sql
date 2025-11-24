-- =====================================================
-- BANKING SYSTEM - NTT DATA TECHNICAL TEST
-- Database Export - 2025-11-24 02:00:50
-- =====================================================

--
-- PostgreSQL database dump
--

\restrict a8h2g5kBSRX01sDYTCmn2Rz91L2iwgcTlPK4ksLWYjdA4wguQWTja4iwz1daOqK

-- Dumped from database version 15.15
-- Dumped by pg_dump version 15.15

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP INDEX IF EXISTS public.idx_customers_status;
DROP INDEX IF EXISTS public.idx_customers_identification;
ALTER TABLE IF EXISTS ONLY public.customers DROP CONSTRAINT IF EXISTS customers_pkey;
ALTER TABLE IF EXISTS ONLY public.customers DROP CONSTRAINT IF EXISTS customers_identification_key;
DROP TABLE IF EXISTS public.customers;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: customers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customers (
    customer_id uuid NOT NULL,
    name character varying(255) NOT NULL,
    gender character varying(50) NOT NULL,
    identification character varying(50) NOT NULL,
    address character varying(500) NOT NULL,
    phone character varying(20) NOT NULL,
    password character varying(255) NOT NULL,
    status boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.customers OWNER TO postgres;

--
-- Data for Name: customers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.customers (customer_id, name, gender, identification, address, phone, password, status, created_at, updated_at) FROM stdin;
e83f7e5d-07ac-4adf-a92e-984a617d4b73	Jose Lema	MALE	1234567890	Otavalo sn y principal	098254785	1234	t	2025-11-24 06:57:10.783813	2025-11-24 06:57:10.783813
644bf571-525e-4ad2-a4ae-774d2962858c	Marianela Montalvo	FEMALE	0987654321	Amazonas y NNUU	097548965	5678	t	2025-11-24 06:57:11.461959	2025-11-24 06:57:11.461959
96b6980b-45a7-4cb0-938b-6c9c20cbf490	Juan Osorio	MALE	1122334455	13 junio y Equinoccial	098874587	1245	t	2025-11-24 06:57:11.501133	2025-11-24 06:57:11.501133
\.


--
-- Name: customers customers_identification_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT customers_identification_key UNIQUE (identification);


--
-- Name: customers customers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT customers_pkey PRIMARY KEY (customer_id);


--
-- Name: idx_customers_identification; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_customers_identification ON public.customers USING btree (identification);


--
-- Name: idx_customers_status; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_customers_status ON public.customers USING btree (status);


--
-- PostgreSQL database dump complete
--

\unrestrict a8h2g5kBSRX01sDYTCmn2Rz91L2iwgcTlPK4ksLWYjdA4wguQWTja4iwz1daOqK



-- =====================================================
-- ACCOUNT DATABASE (account_db)
-- =====================================================

--
-- PostgreSQL database dump
--

\restrict 9QA8S9L8N2PPyeHjN7RrxKITOCdFioz3Y6GbUjCIa2az0x0dnDgeqqAVjTPXcDy

-- Dumped from database version 15.15
-- Dumped by pg_dump version 15.15

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE IF EXISTS ONLY public.accounts DROP CONSTRAINT IF EXISTS fk_customer;
ALTER TABLE IF EXISTS ONLY public.movements DROP CONSTRAINT IF EXISTS fk_account;
DROP INDEX IF EXISTS public.idx_movements_date_account;
DROP INDEX IF EXISTS public.idx_movements_date;
DROP INDEX IF EXISTS public.idx_movements_account_id;
DROP INDEX IF EXISTS public.idx_accounts_status;
DROP INDEX IF EXISTS public.idx_accounts_customer_id;
DROP INDEX IF EXISTS public.idx_accounts_account_number;
ALTER TABLE IF EXISTS ONLY public.movements DROP CONSTRAINT IF EXISTS movements_pkey;
ALTER TABLE IF EXISTS ONLY public.customer_info DROP CONSTRAINT IF EXISTS customer_info_pkey;
ALTER TABLE IF EXISTS ONLY public.accounts DROP CONSTRAINT IF EXISTS accounts_pkey;
ALTER TABLE IF EXISTS ONLY public.accounts DROP CONSTRAINT IF EXISTS accounts_account_number_key;
DROP VIEW IF EXISTS public.v_account_statement;
DROP TABLE IF EXISTS public.movements;
DROP TABLE IF EXISTS public.customer_info;
DROP TABLE IF EXISTS public.accounts;
DROP FUNCTION IF EXISTS public.get_account_balance_history(p_account_id uuid, p_start_date timestamp without time zone, p_end_date timestamp without time zone);
--
-- Name: get_account_balance_history(uuid, timestamp without time zone, timestamp without time zone); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_account_balance_history(p_account_id uuid, p_start_date timestamp without time zone, p_end_date timestamp without time zone) RETURNS TABLE(movement_date timestamp without time zone, movement_type character varying, value numeric, balance numeric)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT m.date, m.movement_type, m.value, m.balance
    FROM movements m
    WHERE m.account_id = p_account_id
      AND m.date BETWEEN p_start_date AND p_end_date
    ORDER BY m.date DESC;
END;
$$;


ALTER FUNCTION public.get_account_balance_history(p_account_id uuid, p_start_date timestamp without time zone, p_end_date timestamp without time zone) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: accounts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.accounts (
    account_id uuid NOT NULL,
    account_number character varying(50) NOT NULL,
    account_type character varying(50) NOT NULL,
    current_balance numeric(15,2) DEFAULT 0.00 NOT NULL,
    status boolean DEFAULT true NOT NULL,
    customer_id uuid NOT NULL,
    customer_name character varying(255) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.accounts OWNER TO postgres;

--
-- Name: customer_info; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customer_info (
    customer_id uuid NOT NULL,
    customer_name character varying(255) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.customer_info OWNER TO postgres;

--
-- Name: movements; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.movements (
    movement_id uuid NOT NULL,
    date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    movement_type character varying(50) NOT NULL,
    value numeric(15,2) NOT NULL,
    balance numeric(15,2) NOT NULL,
    account_id uuid NOT NULL,
    account_number character varying(50) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.movements OWNER TO postgres;

--
-- Name: v_account_statement; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.v_account_statement AS
 SELECT a.customer_id,
    a.customer_name,
    a.account_number,
    a.account_type,
    a.current_balance,
    m.movement_id,
    m.date,
    m.movement_type,
    m.value,
    m.balance
   FROM (public.accounts a
     LEFT JOIN public.movements m ON ((a.account_id = m.account_id)))
  ORDER BY a.customer_id, a.account_number, m.date DESC;


ALTER TABLE public.v_account_statement OWNER TO postgres;

--
-- Data for Name: accounts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.accounts (account_id, account_number, account_type, current_balance, status, customer_id, customer_name, created_at, updated_at) FROM stdin;
32f7f4d5-5cde-4366-8b18-b9642e8a612a	585545	CHECKING	1000.00	t	e83f7e5d-07ac-4adf-a92e-984a617d4b73		2025-11-24 06:57:16.943678	2025-11-24 06:57:16.943678
653834ec-239a-4fdd-ba15-3fad64a286e0	478758	SAVING	1425.00	t	e83f7e5d-07ac-4adf-a92e-984a617d4b73		2025-11-24 06:57:16.777172	2025-11-24 06:57:16.777172
ae41dcd7-45c0-4e10-a1a2-fa4823051dcf	225487	CHECKING	700.00	t	644bf571-525e-4ad2-a4ae-774d2962858c		2025-11-24 06:57:16.839308	2025-11-24 06:57:16.839308
e79dcf8f-7729-4c7a-8723-03484ab86f7e	495878	SAVING	150.00	t	96b6980b-45a7-4cb0-938b-6c9c20cbf490		2025-11-24 06:57:16.876146	2025-11-24 06:57:16.876146
a8960417-8740-44a8-9ffb-4c83a2d96a73	496825	SAVING	0.00	t	644bf571-525e-4ad2-a4ae-774d2962858c		2025-11-24 06:57:16.912048	2025-11-24 06:57:16.912048
\.


--
-- Data for Name: customer_info; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.customer_info (customer_id, customer_name, created_at, updated_at) FROM stdin;
e83f7e5d-07ac-4adf-a92e-984a617d4b73	Jose Lema	2025-11-24 06:57:11.919614	2025-11-24 06:57:11.919614
96b6980b-45a7-4cb0-938b-6c9c20cbf490	Juan Osorio	2025-11-24 06:57:11.920921	2025-11-24 06:57:11.920921
644bf571-525e-4ad2-a4ae-774d2962858c	Marianela Montalvo	2025-11-24 06:57:11.919613	2025-11-24 06:57:11.919613
\.


--
-- Data for Name: movements; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.movements (movement_id, date, movement_type, value, balance, account_id, account_number, created_at) FROM stdin;
60bfef0f-3ff1-465d-a31f-4e38079cd30c	2025-11-24 06:57:17.012286	DEBIT	575.00	1425.00	653834ec-239a-4fdd-ba15-3fad64a286e0	478758	2025-11-24 06:57:17.001385
a9b946bc-fe60-4b2c-821d-abc3914d9311	2025-11-24 06:57:17.086932	CREDIT	600.00	700.00	ae41dcd7-45c0-4e10-a1a2-fa4823051dcf	225487	2025-11-24 06:57:17.083782
3b2bb19d-6799-476d-bc1e-b8fc27c0d8bf	2025-11-24 06:57:17.123403	CREDIT	150.00	150.00	e79dcf8f-7729-4c7a-8723-03484ab86f7e	495878	2025-11-24 06:57:17.120022
fdb91e79-0c33-41fe-bca8-358774701a4a	2025-11-24 06:57:17.161534	DEBIT	540.00	0.00	a8960417-8740-44a8-9ffb-4c83a2d96a73	496825	2025-11-24 06:57:17.157455
\.


--
-- Name: accounts accounts_account_number_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.accounts
    ADD CONSTRAINT accounts_account_number_key UNIQUE (account_number);


--
-- Name: accounts accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.accounts
    ADD CONSTRAINT accounts_pkey PRIMARY KEY (account_id);


--
-- Name: customer_info customer_info_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer_info
    ADD CONSTRAINT customer_info_pkey PRIMARY KEY (customer_id);


--
-- Name: movements movements_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.movements
    ADD CONSTRAINT movements_pkey PRIMARY KEY (movement_id);


--
-- Name: idx_accounts_account_number; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_accounts_account_number ON public.accounts USING btree (account_number);


--
-- Name: idx_accounts_customer_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_accounts_customer_id ON public.accounts USING btree (customer_id);


--
-- Name: idx_accounts_status; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_accounts_status ON public.accounts USING btree (status);


--
-- Name: idx_movements_account_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_movements_account_id ON public.movements USING btree (account_id);


--
-- Name: idx_movements_date; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_movements_date ON public.movements USING btree (date);


--
-- Name: idx_movements_date_account; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_movements_date_account ON public.movements USING btree (account_id, date);


--
-- Name: movements fk_account; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.movements
    ADD CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES public.accounts(account_id);


--
-- Name: accounts fk_customer; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.accounts
    ADD CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES public.customer_info(customer_id);


--
-- PostgreSQL database dump complete
--

\unrestrict 9QA8S9L8N2PPyeHjN7RrxKITOCdFioz3Y6GbUjCIa2az0x0dnDgeqqAVjTPXcDy

